package com.example.opencode_mobile.ui.screens.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.opencode_mobile.data.api.FileDiffDto
import com.example.opencode_mobile.ui.components.DiffCard
import com.example.opencode_mobile.ui.components.DiffLine
import com.example.opencode_mobile.ui.components.DiffLineType
import com.example.opencode_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    sessionId: String,
    onBackClick: () -> Unit,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadDiff(sessionId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Review") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarBg,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            uiState.error != null -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(uiState.error ?: "", color = ErrorRed, fontSize = 14.sp)
                        Spacer(Modifier.height(12.dp))
                        IconButton(onClick = { viewModel.loadDiff(sessionId) }) {
                            Icon(Icons.Default.Refresh, "Retry", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
            uiState.diffs.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("No changes to review", color = MaterialTheme.colorScheme.outline, fontSize = 16.sp)
                    }
                }
            }
            else -> {
                var allCollapsed by remember { mutableStateOf(false) }
                val totalAdditions = uiState.diffs.sumOf { it.additions ?: 0 }
                val totalDeletions = uiState.diffs.sumOf { it.deletions ?: 0 }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Review Changes",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${uiState.diffs.size} file${if (uiState.diffs.size != 1) "s" else ""} changed",
                                    color = MaterialTheme.colorScheme.outline,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "+$totalAdditions",
                                    color = SuccessGreen,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = "-$totalDeletions",
                                    color = ErrorRed,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (allCollapsed) "Expand all" else "Collapse all",
                                color = MaterialTheme.colorScheme.outline,
                                fontSize = 15.sp,
                                modifier = Modifier.clickable { allCollapsed = !allCollapsed }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Divider)
                        Spacer(Modifier.height(8.dp))
                    }

                    items(uiState.diffs, key = { it.file ?: it.hashCode().toString() }) { diff ->
                        DiffCard(
                            fileName = diff.file ?: "unknown",
                            additions = diff.additions ?: 0,
                            deletions = diff.deletions ?: 0,
                            diffLines = parsePatch(diff.patch),
                            initialExpanded = !allCollapsed
                        )
                    }
                }
            }
        }
    }
}

private fun parsePatch(patch: String?): List<DiffLine> {
    if (patch.isNullOrBlank()) return emptyList()
    return patch.lines().mapNotNull { line ->
        when {
            line.startsWith("+") && !line.startsWith("+++") ->
                DiffLine(DiffLineType.Addition, line)
            line.startsWith("-") && !line.startsWith("---") ->
                DiffLine(DiffLineType.Deletion, line)
            line.startsWith(" ") ->
                DiffLine(DiffLineType.Normal, line)
            else -> null
        }
    }
}
