package com.example.opencode_mobile.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.opencode_mobile.data.local.Connection
import com.example.opencode_mobile.data.local.ConnectionManager
import com.example.opencode_mobile.data.local.ConnectionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class HomeUiState(
    val connections: List<Connection> = emptyList(),
    val activeConnectionId: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectionStore: ConnectionStore,
    private val connectionManager: ConnectionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                connectionStore.connections,
                connectionStore.activeConnectionId
            ) { connections, activeId ->
                HomeUiState(
                    connections = connections,
                    activeConnectionId = activeId,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun addConnection(name: String, address: String, port: Int, password: String, useTls: Boolean = false) {
        viewModelScope.launch {
            val connection = Connection(
                id = UUID.randomUUID().toString(),
                name = name.ifBlank { address },
                address = address,
                port = port,
                password = password.ifBlank { null },
                useTls = useTls
            )
            connectionStore.addConnection(connection)
        }
    }

    fun updateConnection(id: String, name: String, address: String, port: Int, password: String, useTls: Boolean = false) {
        viewModelScope.launch {
            connectionStore.updateConnection(
                Connection(
                    id = id,
                    name = name.ifBlank { address },
                    address = address,
                    port = port,
                    password = password.ifBlank { null },
                    useTls = useTls
                )
            )
        }
    }

    fun deleteConnection(connection: Connection) {
        viewModelScope.launch {
            connectionStore.deleteConnection(connection.id)
        }
    }

    fun setActiveConnection(connection: Connection) {
        viewModelScope.launch {
            connectionStore.setActiveConnection(connection.id)
            connectionManager.setConnection(connection)
        }
    }
}
