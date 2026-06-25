package com.example.opencode_mobile.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opencode_mobile.data.api.*
import com.example.opencode_mobile.data.local.ConnectionManager
import com.example.opencode_mobile.data.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.sse.EventSource
import timber.log.Timber
import javax.inject.Inject

data class ChatUiState(
    val messages: List<MessageWithPartsDto> = emptyList(),
    val streamingMessageId: String? = null,
    val streamingParts: List<PartDto> = emptyList(),
    val streamingText: String = "",
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val streamingError: Boolean = false,
    val error: String? = null,
    val inputText: String = ""
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val apiServiceProvider: ApiServiceProvider,
    private val okHttpClient: OkHttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var sseEventSource: EventSource? = null
    private var reloadJob: Job? = null

    private var sessionId: String = ""

    fun initialize(sessionId: String) {
        if (sessionId == this.sessionId && _uiState.value.messages.isNotEmpty()) return
        this.sessionId = sessionId
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val messages = messageRepository.getMessages(sessionId)
                    .sortedBy { it.info.time.created }
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load messages"
                )
            }
        }
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() || _uiState.value.isSending) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true, error = null, streamingError = false)

            try {
                messageRepository.sendMessageAsync(sessionId, trimmed)
                connectSse()
                _uiState.value = _uiState.value.copy(inputText = "")
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSending = false,
                    error = e.message ?: "Failed to send message"
                )
            }
        }
    }

    fun setInputText(text: String) {
        _uiState.value = _uiState.value.copy(inputText = text)
    }

    fun retry() {
        val text = _uiState.value.inputText
        if (text.isNotBlank()) sendMessage(text)
        else loadMessages()
    }

    fun disconnectSse() {
        sseEventSource?.cancel()
        sseEventSource = null
    }

    private fun connectSse() {
        sseEventSource?.cancel()
        val baseUrl = apiServiceProvider.getBaseUrl() ?: return

        sseEventSource = connectSse(baseUrl, okHttpClient,
            createSseListener(
                onEvent = { event -> handleSseEvent(event) },
                onFailure = { handleSseFailure(it) },
                onClosed = { handleSseClosed() }
            )
        )
    }

    private fun handleSseEvent(event: EventDto) {
        val eventSessionId = event.properties.sessionID
        val eventMessageId = event.properties.messageID

        if (eventSessionId != null && eventSessionId != sessionId) return

        when (event.type) {
            "message_start" -> {
                val msgId = eventMessageId ?: event.properties.info?.id ?: return
                _uiState.value = _uiState.value.copy(
                    streamingMessageId = msgId,
                    streamingParts = emptyList(),
                    streamingText = "",
                    streamingError = false
                )
            }
            "part_start" -> {
                val part = event.properties.part ?: return
                _uiState.value = _uiState.value.copy(
                    streamingParts = _uiState.value.streamingParts + part
                )
            }
            "part_delta" -> {
                val partId = event.properties.part?.id
                val delta = event.properties.delta ?: return
                if (partId != null && delta.isNotBlank()) {
                    val updated = _uiState.value.streamingParts.map { part ->
                        if (part.id == partId && part.type == "text") {
                            part.copy(text = (part.text ?: "") + delta)
                        } else part
                    }
                    _uiState.value = _uiState.value.copy(streamingParts = updated)
                } else if (delta.isNotBlank()) {
                    _uiState.value = _uiState.value.copy(
                        streamingText = _uiState.value.streamingText + delta
                    )
                }
            }
            "part_complete" -> {
                val part = event.properties.part
                if (part != null) {
                    val updated = _uiState.value.streamingParts.map { p ->
                        if (p.id == part.id) part else p
                    }
                    _uiState.value = _uiState.value.copy(streamingParts = updated)
                }
            }
            "message_complete", "message_done" -> {
                scheduleReload()
            }
            "error" -> {
                _uiState.value = _uiState.value.copy(streamingError = true)
            }
        }
    }

    private fun handleSseFailure(t: Throwable) {
        _uiState.value = _uiState.value.copy(
            isSending = false,
            error = t.message ?: "SSE connection failed"
        )
    }

    private fun handleSseClosed() {
        if (_uiState.value.isSending) {
            scheduleReload()
        }
    }

    private fun scheduleReload() {
        reloadJob?.cancel()
        reloadJob = viewModelScope.launch {
            delay(500)
            sseEventSource?.cancel()
            sseEventSource = null
            try {
                val messages = messageRepository.getMessages(sessionId)
                    .sortedBy { it.info.time.created }
                _uiState.value = _uiState.value.copy(
                    messages = messages,
                    streamingMessageId = null,
                    streamingParts = emptyList(),
                    streamingText = "",
                    isSending = false
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to reload messages after SSE")
                _uiState.value = _uiState.value.copy(isSending = false)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        sseEventSource?.cancel()
    }
}
