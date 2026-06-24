package com.example.opencode_mobile.ui.navigation

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val PROJECTS = "projects/{connectionId}"
    const val SESSIONS = "sessions/{projectId}/{projectWorktree}"
    const val CHAT = "chat/{sessionId}"
    const val REVIEW = "review/{sessionId}"

    fun projects(connectionId: String) = "projects/$connectionId"
    fun sessions(projectId: String, projectWorktree: String) = "sessions/$projectId/$projectWorktree"
    fun chat(sessionId: String) = "chat/$sessionId"
    fun review(sessionId: String) = "review/$sessionId"
}
