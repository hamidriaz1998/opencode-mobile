package com.example.opencode_mobile.data.local

import kotlinx.serialization.Serializable

@Serializable
data class Connection(
    val id: String,
    val name: String,
    val address: String,
    val port: Int = 4096,
    val password: String? = null,
    val isActive: Boolean = false,
    val useTls: Boolean = false
) {
    val scheme: String get() = if (useTls) "https" else "http"
}
