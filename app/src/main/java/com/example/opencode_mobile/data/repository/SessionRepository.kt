package com.example.opencode_mobile.data.repository

import com.example.opencode_mobile.data.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val apiProvider: ApiServiceProvider
) {
    private val api get() = apiProvider.getApi()
    suspend fun getSessions(
        directory: String? = null,
        roots: Boolean? = null,
        limit: Int? = null,
        offset: Int? = null,
        search: String? = null
    ): List<SessionDto> {
        return api.getSessions(directory, roots, limit, offset, search)
    }

    suspend fun getSession(id: String): SessionDto {
        return api.getSession(id)
    }

    suspend fun createSession(title: String? = null, parentID: String? = null): SessionDto {
        return api.createSession(CreateSessionRequest(title = title, parentID = parentID))
    }

    suspend fun deleteSession(id: String): Boolean {
        return api.deleteSession(id)
    }

    suspend fun updateSessionTitle(id: String, title: String): SessionDto {
        return api.updateSession(id, mapOf("title" to title))
    }

    suspend fun getSessionDiff(id: String, messageID: String? = null): List<FileDiffDto> {
        return api.getSessionDiff(id, messageID)
    }

    suspend fun abortSession(id: String): Boolean {
        return api.abortSession(id)
    }

    suspend fun revertMessage(sessionId: String, messageID: String, partID: String? = null): Boolean {
        val body = mutableMapOf("messageID" to messageID)
        if (partID != null) body["partID"] = partID
        return api.revertMessage(sessionId, body)
    }
}
