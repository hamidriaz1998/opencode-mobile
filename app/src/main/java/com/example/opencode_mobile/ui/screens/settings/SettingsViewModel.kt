package com.example.opencode_mobile.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opencode_mobile.data.api.ApiServiceProvider
import com.example.opencode_mobile.data.local.ConnectionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val serverVersion: String? = null,
    val isConnected: Boolean = false,
    val connectionCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val connectionStore: ConnectionStore,
    private val apiServiceProvider: ApiServiceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            connectionStore.connections.collect { connections ->
                _uiState.value = _uiState.value.copy(connectionCount = connections.size)
            }
        }
        checkServerHealth()
    }

    fun checkServerHealth() {
        viewModelScope.launch {
            try {
                val health = apiServiceProvider.getApi().health()
                _uiState.value = _uiState.value.copy(
                    serverVersion = health.version,
                    isConnected = health.healthy
                )
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(
                    serverVersion = null,
                    isConnected = false
                )
            }
        }
    }

    fun clearAllConnections() {
        viewModelScope.launch {
            val connections = connectionStore.connections.value
            connections.forEach { connectionStore.deleteConnection(it.id) }
        }
    }
}
