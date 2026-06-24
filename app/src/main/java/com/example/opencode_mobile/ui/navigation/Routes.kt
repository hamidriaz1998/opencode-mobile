package com.example.opencode_mobile.ui.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val PROJECTS = "projects/{connectionId}"
    const val SESSIONS = "sessions/{projectId}/{projectWorktree}"
    const val CHAT = "chat/{sessionId}"
    const val REVIEW = "review/{sessionId}"

    fun projects(connectionId: String) = "projects/$connectionId"
    fun sessions(projectId: String, projectWorktree: String): String {
        val encoded = java.net.URLEncoder.encode(projectWorktree, "UTF-8")
        return "sessions/$projectId/$encoded"
    }
    fun chat(sessionId: String): String {
        val encoded = java.net.URLEncoder.encode(sessionId, "UTF-8")
        return "chat/$encoded"
    }

    fun review(sessionId: String): String {
        val encoded = java.net.URLEncoder.encode(sessionId, "UTF-8")
        return "review/$encoded"
    }
}
