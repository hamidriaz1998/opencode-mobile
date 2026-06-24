package com.example.opencode_mobile.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SettingsSection("Server") {
                Text(
                    text = "Connect to an opencode server\nfrom the Home tab.",
                    color = Color(0xFF998D97),
                    fontSize = 14.sp
                )
            }

            SettingsSection("Appearance") {
                Text(
                    text = "Dark theme only (for now).",
                    color = Color(0xFF998D97),
                    fontSize = 14.sp
                )
            }

            SettingsSection("About") {
                Text(
                    text = "OpenCode Mobile v1.0.0",
                    color = Color(0xFFE9E1DF),
                    fontSize = 14.sp
                )
                Text(
                    text = "Mobile client for opencode coding agent.",
                    color = Color(0xFF998D97),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141110)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = Color(0xFFEDB2F1),
                fontSize = 14.sp
            )
            content()
        }
    }
}
