package com.example.opencode_mobile.data.repository

import com.example.opencode_mobile.data.api.ApiServiceProvider
import com.example.opencode_mobile.data.api.ProjectDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val apiProvider: ApiServiceProvider
) {
    private val api get() = apiProvider.getApi()

    suspend fun getProjects(): List<ProjectDto> {
        return api.getProjects()
    }

    suspend fun getCurrentProject(): ProjectDto {
        return api.getCurrentProject()
    }
}
