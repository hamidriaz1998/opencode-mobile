package com.example.opencode_mobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val json = Json { ignoreUnknownKeys = true }

@Singleton
class ConnectionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "connection_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val _connections = MutableStateFlow<List<Connection>>(emptyList())
    val connections: Flow<List<Connection>> = _connections.asStateFlow()

    private val _activeConnectionId = MutableStateFlow<String?>(null)
    val activeConnectionId: Flow<String?> = _activeConnectionId.asStateFlow()

    private val _activeConnection = MutableStateFlow<Connection?>(null)
    val activeConnection: Flow<Connection?> = _activeConnection.asStateFlow()

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        when (key) {
            CONNECTIONS_KEY -> _connections.value = readConnectionsSync()
            ACTIVE_CONNECTION_ID_KEY -> {
                val id = prefs.getString(ACTIVE_CONNECTION_ID_KEY, null)
                _activeConnectionId.value = id
                _activeConnection.value = _connections.value.find { it.id == id }
            }
        }
    }

    init {
        prefs.registerOnSharedPreferenceChangeListener(listener)
        _connections.value = readConnectionsSync()
        val id = prefs.getString(ACTIVE_CONNECTION_ID_KEY, null)
        _activeConnectionId.value = id
        _activeConnection.value = _connections.value.find { it.id == id }
    }

    private fun readConnectionsSync(): List<Connection> {
        val raw = prefs.getString(CONNECTIONS_KEY, "[]") ?: "[]"
        return try {
            json.decodeFromString<List<Connection>>(raw)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode connections")
            emptyList()
        }
    }

    suspend fun addConnection(connection: Connection) {
        withContext(Dispatchers.IO) {
            val list = _connections.value.toMutableList()
            list.add(connection)
            prefs.edit().putString(CONNECTIONS_KEY, json.encodeToString(list)).apply()
        }
    }

    suspend fun updateConnection(connection: Connection) {
        withContext(Dispatchers.IO) {
            val list = _connections.value.toMutableList()
            val index = list.indexOfFirst { it.id == connection.id }
            if (index >= 0) {
                list[index] = connection
                prefs.edit().putString(CONNECTIONS_KEY, json.encodeToString(list)).apply()
            }
        }
    }

    suspend fun deleteConnection(id: String) {
        withContext(Dispatchers.IO) {
            val list = _connections.value.toMutableList()
            list.removeAll { it.id == id }
            prefs.edit().putString(CONNECTIONS_KEY, json.encodeToString(list)).apply()
            if (prefs.getString(ACTIVE_CONNECTION_ID_KEY, null) == id) {
                prefs.edit().remove(ACTIVE_CONNECTION_ID_KEY).apply()
            }
        }
    }

    suspend fun setActiveConnection(id: String?) {
        withContext(Dispatchers.IO) {
            if (id != null) {
                prefs.edit().putString(ACTIVE_CONNECTION_ID_KEY, id).apply()
            } else {
                prefs.edit().remove(ACTIVE_CONNECTION_ID_KEY).apply()
            }
        }
    }

    companion object {
        private const val CONNECTIONS_KEY = "connections"
        private const val ACTIVE_CONNECTION_ID_KEY = "active_connection_id"
    }
}
