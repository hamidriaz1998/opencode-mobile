package com.example.opencode_mobile.data.api

import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.*
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

interface OpencodeApiService {

    @GET("global/health")
    suspend fun health(): HealthResponse

    @GET("project")
    suspend fun getProjects(): List<ProjectDto>

    @GET("project/current")
    suspend fun getCurrentProject(): ProjectDto

    @GET("session")
    suspend fun getSessions(
        @Query("directory") directory: String? = null,
        @Query("roots") roots: Boolean? = null,
        @Query("limit") limit: Int? = null,
        @Query("offset") offset: Int? = null,
        @Query("search") search: String? = null,
        @Query("start") start: Long? = null
    ): List<SessionDto>

    @POST("session")
    suspend fun createSession(@Body body: CreateSessionRequest): SessionDto

    @GET("session/{id}")
    suspend fun getSession(@Path("id") id: String): SessionDto

    @DELETE("session/{id}")
    suspend fun deleteSession(@Path("id") id: String): Boolean

    @PATCH("session/{id}")
    suspend fun updateSession(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): SessionDto

    @GET("session/{id}/children")
    suspend fun getSessionChildren(@Path("id") id: String): List<SessionDto>

    @GET("session/{id}/diff")
    suspend fun getSessionDiff(
        @Path("id") id: String,
        @Query("messageID") messageID: String? = null
    ): List<FileDiffDto>

    @GET("session/{id}/message")
    suspend fun getMessages(
        @Path("id") id: String,
        @Query("limit") limit: Int? = null,
        @Query("before") before: String? = null
    ): List<MessageWithPartsDto>

    @POST("session/{id}/message")
    suspend fun sendMessage(
        @Path("id") id: String,
        @Body body: SendMessageRequest
    ): MessageWithPartsDto

    @POST("session/{id}/prompt_async")
    suspend fun sendMessageAsync(
        @Path("id") id: String,
        @Body body: SendMessageRequest
    ): Response<Unit>

    @POST("session/{id}/abort")
    suspend fun abortSession(@Path("id") id: String): Boolean

    @POST("session/{id}/revert")
    suspend fun revertMessage(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Boolean

    @GET("agent")
    suspend fun getAgents(): List<AgentDto>

    @GET("command")
    suspend fun getCommands(): List<CommandDto>

    @GET("session/status")
    suspend fun getSessionStatus(): Map<String, String>
}

fun createApiService(baseUrl: String, okHttpClient: OkHttpClient): OpencodeApiService {
    val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }
    val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    return Retrofit.Builder()
        .baseUrl(url)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OpencodeApiService::class.java)
}

private val sseJson = Json { ignoreUnknownKeys = true; isLenient = true }

fun createSseListener(
    onEvent: (EventDto) -> Unit,
    onFailure: (Throwable) -> Unit = {},
    onClosed: () -> Unit = {}
): EventSourceListener {
    return object : EventSourceListener() {
        override fun onEvent(eventSource: okhttp3.sse.EventSource, id: String?, type: String?, data: String) {
            if (data.isNotBlank()) {
                try {
                    val event = sseJson.decodeFromString<EventDto>(data)
                    onEvent(event)
                } catch (_: Exception) { }
            }
        }

        override fun onFailure(eventSource: okhttp3.sse.EventSource, t: Throwable?, response: okhttp3.Response?) {
            if (t != null) onFailure(t)
        }

        override fun onClosed(eventSource: okhttp3.sse.EventSource) {
            onClosed()
        }
    }
}

fun connectSse(
    baseUrl: String,
    okHttpClient: OkHttpClient,
    listener: EventSourceListener
): okhttp3.sse.EventSource {
    val url = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
    val request = okhttp3.Request.Builder()
        .url("${url}event")
        .header("Accept", "text/event-stream")
        .build()
    val factory = EventSources.createFactory(okHttpClient)
    return factory.newEventSource(request, listener)
}
