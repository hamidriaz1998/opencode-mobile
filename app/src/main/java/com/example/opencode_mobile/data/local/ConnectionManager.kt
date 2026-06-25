package com.example.opencode_mobile.data.local

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionManager @Inject constructor(
    private val connectionStore: ConnectionStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _activeConnection = MutableStateFlow<Connection?>(null)
    val activeConnection: StateFlow<Connection?> = _activeConnection.asStateFlow()

    fun setConnection(connection: Connection?) {
        _activeConnection.value = connection
        if (connection != null) {
            scope.launch {
                connectionStore.setActiveConnection(connection.id)
            }
        }
    }
}
