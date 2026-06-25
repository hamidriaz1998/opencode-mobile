package com.example.opencode_mobile.di

import com.example.opencode_mobile.data.local.ConnectionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Base64
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptor

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @AuthInterceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(connectionManager: ConnectionManager): Interceptor {
        return Interceptor { chain ->
            val connection = connectionManager.activeConnection.value
            val request = chain.request().newBuilder().apply {
                if (connection?.password != null && connection.password!!.isNotBlank()) {
                    val credentials = "opencode:${connection.password}"
                    val encoded = Base64.getEncoder().encodeToString(credentials.toByteArray())
                    addHeader("Authorization", "Basic $encoded")
                }
            }.build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(@AuthInterceptor authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }
}
