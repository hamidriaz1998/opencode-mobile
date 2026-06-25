package com.example.opencode_mobile.ui.screens.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opencode_mobile.data.api.SessionDto
import com.example.opencode_mobile.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionsUiState(
    val sessions: List<SessionDto> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val error: String? = null,
    val sortAscending: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class SessionsViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    private var lastLoadedDirectory: String? = null

    fun loadSessions(directory: String? = null) {
        if (directory == lastLoadedDirectory && _uiState.value.sessions.isNotEmpty()) return
        lastLoadedDirectory = directory
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val sessions = sessionRepository.getSessions(
                    directory = directory,
                    roots = true,
                    limit = 50,
                    offset = 0
                )
                _uiState.value = _uiState.value.copy(
                    sessions = sessions,
                    hasMore = sessions.size >= 50,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load sessions"
                )
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (!state.hasMore || state.isLoadingMore) return
        _uiState.value = state.copy(isLoadingMore = true)
        viewModelScope.launch {
            try {
                val next = sessionRepository.getSessions(
                    directory = lastLoadedDirectory,
                    roots = true,
                    limit = 50,
                    offset = state.sessions.size
                )
                _uiState.value = _uiState.value.copy(
                    sessions = _uiState.value.sessions + next,
                    hasMore = next.size >= 50,
                    isLoadingMore = false
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }

    fun toggleSort() {
        _uiState.value = _uiState.value.copy(sortAscending = !_uiState.value.sortAscending)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getSortedFilteredSessions(): List<SessionDto> {
        val state = _uiState.value
        val filtered = if (state.searchQuery.isBlank()) {
            state.sessions
        } else {
            state.sessions.filter {
                val title = it.title ?: it.slug ?: ""
                title.contains(state.searchQuery, ignoreCase = true)
            }
        }
        return if (state.sortAscending) {
            filtered.sortedBy { it.time.updated ?: it.time.created }
        } else {
            filtered.sortedByDescending { it.time.updated ?: it.time.created }
        }
    }
}
