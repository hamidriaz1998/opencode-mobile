package com.example.opencode_mobile.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.opencode_mobile.data.api.MessageWithPartsDto
import com.example.opencode_mobile.data.api.PartDto
import com.example.opencode_mobile.ui.components.DiffCard
import com.example.opencode_mobile.ui.components.DiffLine
import com.example.opencode_mobile.ui.components.DiffLineType
import com.example.opencode_mobile.ui.theme.*
import ru.wertik.orca.compose.Orca
import ru.wertik.orca.compose.OrcaRootLayout
import ru.wertik.orca.compose.OrcaSecurityPolicies
import ru.wertik.orca.compose.OrcaStyle
import ru.wertik.orca.compose.material3.rememberOrcaMaterialStyle
import ru.wertik.orca.core.OrcaMarkdownParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(sessionId) {
        viewModel.initialize(sessionId)
    }

    LaunchedEffect(uiState.messages.size, uiState.streamingParts.size) {
        if (uiState.messages.isNotEmpty() || uiState.streamingParts.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Session",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                actions = {
                    Button(
                        onClick = onReviewClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text("Review", fontSize = 14.sp)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.MoreVert, "More options", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarBg,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            ChatInputBar(
                text = uiState.inputText,
                isSending = uiState.isSending,
                onTextChange = { viewModel.setInputText(it) },
                onSend = { viewModel.sendMessage(it) }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null && uiState.messages.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error ?: "", color = ErrorRed, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        IconButton(onClick = { viewModel.retry() }) {
                            Icon(Icons.Default.Refresh, "Retry", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            else -> {
                val allMessages = remember(uiState.messages, uiState.streamingParts, uiState.streamingText) {
                    buildList {
                        uiState.messages.forEach { msg ->
                            if (msg.info.role == "user") {
                                val text = msg.parts
                                    .filter { it.type == "text" }
                                    .mapNotNull { it.text }
                                    .joinToString("\n")
                                add(DisplayMessage.User(text = text))
                            } else {
                                add(DisplayMessage.Agent(message = msg))
                            }
                        }
                        if (uiState.streamingParts.isNotEmpty() || uiState.streamingText.isNotBlank()) {
                            add(DisplayMessage.Streaming(
                                parts = uiState.streamingParts,
                                text = uiState.streamingText
                            ))
                        }
                    }
                }

                val orcaStyle = rememberOrcaMaterialStyle()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = listState,
                    reverseLayout = true,
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(allMessages, key = { it.itemKey }) { item ->
                        when (item) {
                            is DisplayMessage.User -> UserMessageBubble(item.text)
                            is DisplayMessage.Agent -> AgentMessageContent(
                                message = item.message,
                                isLast = item == allMessages.firstOrNull(),
                                orcaStyle = orcaStyle
                            )
                            is DisplayMessage.Streaming -> StreamingContent(
                                parts = item.parts,
                                text = item.text,
                                orcaStyle = orcaStyle
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserMessageBubble(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(UserBubbleBg, RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun AgentMessageContent(
    message: MessageWithPartsDto,
    isLast: Boolean,
    orcaStyle: OrcaStyle
) {
    val orcaParser = remember { OrcaMarkdownParser() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        message.parts.forEach { part ->
            when (part.type) {
                "text" -> {
                    val text = part.text ?: return@forEach
                    if (text.isNotBlank()) {
                        Orca(
                            markdown = text,
                            modifier = Modifier.padding(vertical = 4.dp),
                            parser = orcaParser,
                            parseCacheKey = part.id,
                            style = orcaStyle,
                            rootLayout = OrcaRootLayout.COLUMN,
                            securityPolicy = OrcaSecurityPolicies.Default,
                        )
                    }
                }
                "tool" -> ToolPartCard(part = part)
                "diff" -> DiffPartCard(part = part)
                "reasoning" -> ReasoningPartCard(part = part)
            }
        }
    }
}

@Composable
private fun StreamingContent(
    parts: List<PartDto>,
    text: String,
    orcaStyle: OrcaStyle
) {
    val orcaParser = remember { OrcaMarkdownParser() }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        parts.forEach { part ->
            when (part.type) {
                "text" -> {
                    val partText = part.text ?: ""
                    if (partText.isNotBlank()) {
                        Orca(
                            markdown = partText,
                            modifier = Modifier.padding(vertical = 4.dp),
                            parser = orcaParser,
                            parseCacheKey = "stream-${part.id}",
                            style = orcaStyle,
                            rootLayout = OrcaRootLayout.COLUMN,
                            securityPolicy = OrcaSecurityPolicies.Default,
                        )
                    }
                }
                "tool" -> ToolPartCard(part = part)
                "diff" -> DiffPartCard(part = part)
                "reasoning" -> ReasoningPartCard(part = part)
            }
        }
        if (text.isNotBlank()) {
            Orca(
                markdown = text,
                modifier = Modifier.padding(vertical = 4.dp),
                parser = orcaParser,
                parseCacheKey = "stream-text",
                style = orcaStyle,
                rootLayout = OrcaRootLayout.COLUMN,
                securityPolicy = OrcaSecurityPolicies.Default,
            )
        }
        if (parts.isEmpty() && text.isBlank()) {
            Text(
                text = "▊",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun ToolPartCard(part: PartDto) {
    val toolName = part.tool ?: "tool"
    val toolState = part.state
    val status = toolState?.status ?: "running"
    val output = toolState?.output ?: ""

    var expanded by remember { mutableStateOf(false) }
    val commandText = toolState?.title ?: toolName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    "complete", "success" -> Icons.Default.CheckCircle
                    "error", "failed" -> Icons.Default.Error
                    else -> Icons.Default.HourglassTop
                },
                contentDescription = null,
                tint = when (status) {
                    "complete", "success" -> SuccessGreen
                    "error", "failed" -> ErrorRed
                    else -> MaterialTheme.colorScheme.primary
                },
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = commandText,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
        if (expanded && output.isNotBlank()) {
            HorizontalDivider(color = Divider)
            Text(
                text = output,
                color = MaterialTheme.colorScheme.outline,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun DiffPartCard(part: PartDto) {
    val snapshot = part.snapshot
    val diffLines = if (snapshot != null) {
        snapshot.lines().map { line ->
            when {
                line.startsWith("+") -> DiffLine(DiffLineType.Addition, line)
                line.startsWith("-") -> DiffLine(DiffLineType.Deletion, line)
                else -> DiffLine(DiffLineType.Normal, line)
            }
        }
    } else emptyList()

    DiffCard(
        fileName = part.tool ?: "diff",
        additions = diffLines.count { it.type == DiffLineType.Addition },
        deletions = diffLines.count { it.type == DiffLineType.Deletion },
        diffLines = diffLines,
        initialExpanded = false,
        modifier = Modifier.padding(vertical = 6.dp)
    )
}

@Composable
private fun ReasoningPartCard(part: PartDto) {
    var expanded by remember { mutableStateOf(false) }
    val reasonText = part.reason ?: part.text ?: ""

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (expanded) "Reasoning" else "Reasoning...",
                color = MaterialTheme.colorScheme.outline,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Toggle",
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(18.dp)
            )
        }
        if (expanded && reasonText.isNotBlank()) {
            HorizontalDivider(color = Divider)
            Text(
                text = reasonText,
                color = SubduedText,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    text: String,
    isSending: Boolean,
    onTextChange: (String) -> Unit,
    onSend: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ToolSurface, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = { Text("Type a message...", color = PlaceholderText) },
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f),
                enabled = !isSending,
                singleLine = true
            )
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(InputContainerBg, RoundedCornerShape(16.dp))
                    .clickable { }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Agent", color = MaterialTheme.colorScheme.onSurface, fontSize = 14.sp)
                Spacer(Modifier.width(4.dp))
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isSending) MaterialTheme.colorScheme.outline else SendButtonBg)
                    .clickable(enabled = !isSending && text.isNotBlank()) { onSend(text) },
                contentAlignment = Alignment.Center
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = SendIconTint
                    )
                }
            }
        }
    }
}

private sealed interface DisplayMessage {
    val itemKey: String
    data class User(val text: String) : DisplayMessage {
        override val itemKey: String get() = "user:$text"
    }
    data class Agent(val message: MessageWithPartsDto) : DisplayMessage {
        override val itemKey: String get() = "agent:${message.info.id}"
    }
    data class Streaming(
        val parts: List<PartDto> = emptyList(),
        val text: String = ""
    ) : DisplayMessage {
        override val itemKey: String get() = "streaming"
    }
}
