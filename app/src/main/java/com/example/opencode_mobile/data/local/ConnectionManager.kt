package com.example.opencode_mobile.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConnectionManager @Inject constructor() {
    private val _activeConnection = MutableStateFlow<Connection?>(null)
    val activeConnection: StateFlow<Connection?> = _activeConnection.asStateFlow()

    fun setConnection(connection: Connection?) {
        _activeConnection.value = connection
    }
}
