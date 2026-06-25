package com.example.opencode_mobile.data.api

import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiServiceProvider @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    @Volatile
    private var currentBaseUrl: String? = null

    @Volatile
    private var service: OpencodeApiService? = null

    fun getApi(): OpencodeApiService {
        synchronized(this) {
            val cached = service
            if (cached != null && currentBaseUrl != null) return cached
            throw IllegalStateException("API service not initialized. Call init(baseUrl) first.")
        }
    }

    fun init(baseUrl: String) {
        val normalized = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        synchronized(this) {
            if (normalized == currentBaseUrl && service != null) return
            currentBaseUrl = normalized
            service = createApiService(normalized, okHttpClient)
        }
    }

    fun getBaseUrl(): String? = currentBaseUrl
}
