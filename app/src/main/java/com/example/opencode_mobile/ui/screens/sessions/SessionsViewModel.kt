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

    fun loadSessions(directory: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val sessions = sessionRepository.getSessions(directory = directory)
                _uiState.value = _uiState.value.copy(
                    sessions = sessions,
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
