package com.example.opencode_mobile.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.opencode_mobile.ui.theme.Opencode_mobileTheme
import com.example.opencode_mobile.ui.components.DarkAppBar
import com.example.opencode_mobile.ui.components.ListItemCard
import com.example.opencode_mobile.ui.components.ListItemData
import com.example.opencode_mobile.ui.components.SearchBar

@Composable
fun SessionsScreen(
    projectId: String,
    onSessionClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionsList = listOf(
        ListItemData("New session - 2026-04-01T20:04:59.753", "12h ago"),
        ListItemData("Electron app login page display issue", "2d ago"),
        ListItemData("Explore Electron login flow (@explore su...", "2d ago"),
        ListItemData("Missing @nodetool/base-nodes module", "2d ago"),
        ListItemData("Control edge connection issue: edges st...", "2/16/2026"),
        ListItemData("Find edge connection code (@explore su...", "2/16/2026")
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            DarkAppBar(
                title = projectId.ifEmpty { "Sessions" },
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* No-op POC */ },
                containerColor = Color(0xFF5A315E),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Session"
                )
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SearchBar(placeholder = "Search sessions...")
            Spacer(modifier = Modifier.height(8.dp))
            ListItemCard(
                items = sessionsList,
                onItemClick = { index ->
                    onSessionClick(sessionsList[index].title)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SessionsScreenPreview() {
    Opencode_mobileTheme {
        SessionsScreen(
            projectId = "nodetool",
            onSessionClick = {},
            onBackClick = {}
        )
    }
}

