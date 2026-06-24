package com.example.opencode_mobile.ui.screens.projects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opencode_mobile.data.api.ProjectDto
import com.example.opencode_mobile.data.repository.ProjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProjectsUiState(
    val projects: List<ProjectDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val sortAscending: Boolean = false,
    val searchQuery: String = ""
)

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProjectsUiState())
    val uiState: StateFlow<ProjectsUiState> = _uiState.asStateFlow()

    init {
        loadProjects()
    }

    fun loadProjects() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val projects = projectRepository.getProjects()
                _uiState.value = _uiState.value.copy(
                    projects = projects,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load projects"
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

    fun getSortedFilteredProjects(): List<ProjectDto> {
        val state = _uiState.value
        val filtered = if (state.searchQuery.isBlank()) {
            state.projects
        } else {
            state.projects.filter {
                it.worktree.contains(state.searchQuery, ignoreCase = true) ||
                    it.id.contains(state.searchQuery, ignoreCase = true)
            }
        }
        return if (state.sortAscending) {
            filtered.sortedBy { it.time.updated ?: it.time.created }
        } else {
            filtered.sortedByDescending { it.time.updated ?: it.time.created }
        }
    }
}
