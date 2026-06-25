package com.example.opencode_mobile.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class HealthResponse(
    val healthy: Boolean,
    val version: String
)

@Serializable
data class ProjectDto(
    val id: String,
    val worktree: String,
    val vcs: String? = null,
    val time: TimeDto
)

@Serializable
data class TimeDto(
    val created: Long,
    val updated: Long? = null
)

@Serializable
data class SessionDto(
    val id: String,
    val slug: String? = null,
    val projectID: String? = null,
    val directory: String? = null,
    val path: String? = null,
    val title: String? = null,
    val agent: String? = null,
    val model: ModelDto? = null,
    val summary: SessionSummaryDto? = null,
    val cost: Double? = null,
    val tokens: TokensDto? = null,
    val version: String? = null,
    val time: TimeDto,
    val parentID: String? = null
)

@Serializable
data class ModelDto(
    val id: String? = null,
    val providerID: String? = null,
    val variant: String? = null
)

@Serializable
data class SessionSummaryDto(
    val additions: Int? = null,
    val deletions: Int? = null,
    val files: Int? = null,
    val diffs: List<FileDiffDto>? = null
)

@Serializable
data class TokensDto(
    val input: Long? = null,
    val output: Long? = null,
    val reasoning: Long? = null,
    val cache: CacheDto? = null
)

@Serializable
data class CacheDto(
    val read: Long? = null,
    val write: Long? = null
)

@Serializable
data class FileDiffDto(
    val file: String? = null,
    val before: String? = null,
    val after: String? = null,
    val additions: Int? = null,
    val deletions: Int? = null,
    val patch: String? = null,
    val status: String? = null
)

@Serializable
data class MessageDto(
    val id: String,
    val sessionID: String,
    val role: String,
    val time: MessageTimeDto,
    val parentID: String? = null,
    val modelID: String? = null,
    val providerID: String? = null,
    val mode: String? = null,
    val agent: String? = null,
    val path: PathDto? = null,
    val cost: Double? = null,
    val tokens: TokensDto? = null,
    val finish: String? = null,
    val summary: JsonElement? = null,
    val error: ErrorDto? = null
)

@Serializable
data class MessageTimeDto(
    val created: Long,
    val completed: Long? = null
)

@Serializable
data class PathDto(
    val cwd: String? = null,
    val root: String? = null
)

@Serializable
data class MessageSummaryDto(
    val title: String? = null,
    val body: String? = null,
    val diffs: List<FileDiffDto>? = null
)

@Serializable
data class ErrorDto(
    val name: String? = null,
    val message: String? = null
)

@Serializable
data class MessageWithPartsDto(
    val info: MessageDto,
    val parts: List<PartDto>
)

@Serializable
data class PartDto(
    val id: String,
    val sessionID: String,
    val messageID: String,
    val type: String,
    val text: String? = null,
    val tool: String? = null,
    val callID: String? = null,
    val state: ToolStateDto? = null,
    val time: PartTimeDto? = null,
    val snapshot: String? = null,
    val reason: String? = null,
    val cost: Double? = null,
    val tokens: TokensDto? = null,
    val synthetic: Boolean? = null,
    val ignored: Boolean? = null,
    val metadata: Map<String, JsonElement>? = null
)

@Serializable
data class ToolStateDto(
    val status: String? = null,
    val input: JsonElement? = null,
    val output: String? = null,
    val title: String? = null
)

@Serializable
data class PartTimeDto(
    val start: Long? = null,
    val end: Long? = null
)

@Serializable
data class CreateSessionRequest(
    val title: String? = null,
    val parentID: String? = null
)

@Serializable
data class SendMessageRequest(
    val parts: List<PartInputDto>,
    val model: ModelDto? = null,
    val agent: String? = null,
    val noReply: Boolean? = null,
    val messageID: String? = null
)

@Serializable
data class PartInputDto(
    val type: String,
    val text: String? = null
)

@Serializable
data class AgentDto(
    val id: String? = null,
    val name: String? = null,
    val description: String? = null
)

@Serializable
data class CommandDto(
    val id: String? = null,
    val command: String? = null,
    val description: String? = null
)

@Serializable
data class EventDto(
    val id: String,
    val type: String,
    val properties: EventPropertiesDto = EventPropertiesDto()
)

@Serializable
data class EventPropertiesDto(
    val info: SessionDto? = null,
    val part: PartDto? = null,
    val delta: String? = null,
    val messageID: String? = null,
    val sessionID: String? = null,
    val status: String? = null
)
