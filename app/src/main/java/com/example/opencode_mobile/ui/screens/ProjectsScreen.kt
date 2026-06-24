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
fun ProjectsScreen(
    onProjectClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val projectsList = listOf(
        ListItemData("nodetool", "/home/user/projects/nodetool"),
        ListItemData("mandlebrot sets visualization", "/home/user/projects/uni/mandlebrot"),
        ListItemData("Plant classification CV", "/home/user/projects/Uni/plant-cv"),
        ListItemData("C-http-server", "/home/user/Desktop/C-http-server"),
        ListItemData("Minideploy", "/home/user/projects/Minideploy"),
        ListItemData("Test", "/home/user/projects/Test")
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            DarkAppBar(
                title = "Projects",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* No-op POC */ },
                containerColor = Color(0xFFEDB2F1), // primary lavender from DESIGN.md
                contentColor = Color(0xFF64336C), // text/icon color from DESIGN.md
                shape = RoundedCornerShape(100.dp), // pill shape from DESIGN.md
                modifier = Modifier.padding(bottom = 16.dp, end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Project"
                )
            }
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            SearchBar(placeholder = "Search projects...")
            Spacer(modifier = Modifier.height(8.dp))
            ListItemCard(
                items = projectsList,
                onItemClick = { index ->
                    onProjectClick(projectsList[index].title)
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectsScreenPreview() {
    Opencode_mobileTheme {
        ProjectsScreen(
            onProjectClick = {},
            onBackClick = {}
        )
    }
}

