package com.example.opencode_mobile.data.repository

import com.example.opencode_mobile.data.api.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val apiProvider: ApiServiceProvider
) {
    private val api get() = apiProvider.getApi()
    suspend fun getMessages(
        sessionId: String,
        limit: Int? = null,
        before: String? = null
    ): List<MessageWithPartsDto> {
        return api.getMessages(sessionId, limit, before)
    }

    suspend fun sendMessage(
        sessionId: String,
        text: String,
        agent: String? = null,
        model: ModelDto? = null
    ): MessageWithPartsDto {
        return api.sendMessage(sessionId, SendMessageRequest(
            parts = listOf(PartInputDto(type = "text", text = text)),
            agent = agent,
            model = model
        ))
    }

    suspend fun sendMessageAsync(
        sessionId: String,
        text: String,
        agent: String? = null,
        model: ModelDto? = null
    ) {
        api.sendMessageAsync(sessionId, SendMessageRequest(
            parts = listOf(PartInputDto(type = "text", text = text)),
            agent = agent,
            model = model
        ))
    }
}
