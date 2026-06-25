package com.example.opencode_mobile.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "connections")

private val json = Json { ignoreUnknownKeys = true }

@Singleton
class ConnectionStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectionsKey = stringPreferencesKey("connections")
    private val activeConnectionIdKey = stringPreferencesKey("active_connection_id")

    val connections: Flow<List<Connection>> = context.dataStore.data.map { prefs ->
        val raw = prefs[connectionsKey] ?: "[]"
        try {
            json.decodeFromString<List<Connection>>(raw)
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode connections")
            emptyList()
        }
    }

    val activeConnectionId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[activeConnectionIdKey]
    }

    val activeConnection: Flow<Connection?> = context.dataStore.data.map { prefs ->
        val raw = prefs[connectionsKey] ?: "[]"
        val id = prefs[activeConnectionIdKey]
        try {
            json.decodeFromString<List<Connection>>(raw).find { it.id == id }
        } catch (e: Exception) {
            Timber.e(e, "Failed to decode active connection")
            null
        }
    }

    suspend fun addConnection(connection: Connection) {
        context.dataStore.edit { prefs ->
            val raw = prefs[connectionsKey] ?: "[]"
            val list = try {
                json.decodeFromString<List<Connection>>(raw).toMutableList()
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode connections in addConnection")
                mutableListOf()
            }
            list.add(connection)
            prefs[connectionsKey] = json.encodeToString(list)
        }
    }

    suspend fun updateConnection(connection: Connection) {
        context.dataStore.edit { prefs ->
            val raw = prefs[connectionsKey] ?: "[]"
            val list = try {
                json.decodeFromString<List<Connection>>(raw).toMutableList()
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode connections in updateConnection")
                mutableListOf()
            }
            val index = list.indexOfFirst { it.id == connection.id }
            if (index >= 0) {
                list[index] = connection
                prefs[connectionsKey] = json.encodeToString(list)
            }
        }
    }

    suspend fun deleteConnection(id: String) {
        context.dataStore.edit { prefs ->
            val raw = prefs[connectionsKey] ?: "[]"
            val list = try {
                json.decodeFromString<List<Connection>>(raw).toMutableList()
            } catch (e: Exception) {
                Timber.e(e, "Failed to decode connections in deleteConnection")
                mutableListOf()
            }
            list.removeAll { it.id == id }
            prefs[connectionsKey] = json.encodeToString(list)
            if (prefs[activeConnectionIdKey] == id) {
                prefs.remove(activeConnectionIdKey)
            }
        }
    }

    suspend fun setActiveConnection(id: String?) {
        context.dataStore.edit { prefs ->
            if (id != null) {
                prefs[activeConnectionIdKey] = id
            } else {
                prefs.remove(activeConnectionIdKey)
            }
        }
    }
}
