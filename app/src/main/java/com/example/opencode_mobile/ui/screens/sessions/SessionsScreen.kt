package com.example.opencode_mobile.ui.screens.sessions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.opencode_mobile.data.api.SessionDto
import com.example.opencode_mobile.ui.components.SearchBar
import com.example.opencode_mobile.util.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    projectWorktree: String,
    onSessionClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: SessionsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val sessions = remember(uiState.sortAscending, uiState.searchQuery, uiState.sessions) {
        viewModel.getSortedFilteredSessions()
    }

    LaunchedEffect(projectWorktree) {
        viewModel.loadSessions(projectWorktree)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = projectWorktree.substringAfterLast("/")
                            .ifEmpty { "Sessions" }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F0E0D),
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF161312)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                placeholder = "Search sessions...",
                onQueryChange = { viewModel.setSearchQuery(it) },
                onSortToggle = { viewModel.toggleSort() },
                sortAscending = uiState.sortAscending
            )

            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFEDB2F1))
                    }
                }
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.error ?: "Unknown error",
                                color = Color(0xFFF44336),
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            IconButton(onClick = { viewModel.loadSessions(projectWorktree) }) {
                                Icon(Icons.Default.Refresh, "Retry", tint = Color(0xFFEDB2F1))
                            }
                        }
                    }
                }
                sessions.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotBlank())
                                "No matching sessions" else "No sessions found",
                            color = Color(0xFF998D97),
                            fontSize = 16.sp
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(sessions, key = { it.id }) { session ->
                            SessionCard(
                                session = session,
                                onClick = { onSessionClick(session.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: SessionDto,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141110)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title ?: session.slug ?: "Untitled session",
                    color = Color(0xFFE9E1DF),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row {
                    Text(
                        text = session.agent ?: "unknown agent",
                        color = Color(0xFF6B646A),
                        fontSize = 13.sp
                    )
                    if (session.model != null) {
                        Text(
                            text = " · ${session.model.id ?: ""}",
                            color = Color(0xFF6B646A),
                            fontSize = 13.sp
                        )
                    }
                }
                val timestamp = session.time.updated ?: session.time.created
                Text(
                    text = formatTimestamp(timestamp),
                    color = Color(0xFF6B646A),
                    fontSize = 12.sp
                )
            }
            if (session.summary != null) {
                val additions = session.summary.additions ?: 0
                val deletions = session.summary.deletions ?: 0
                if (additions > 0 || deletions > 0) {
                    Text(
                        text = "+$additions -$deletions",
                        color = Color(0xFF998D97),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}


