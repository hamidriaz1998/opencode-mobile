package com.example.opencode_mobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.opencode_mobile.ui.theme.Opencode_mobileTheme
import com.example.opencode_mobile.ui.components.DarkAppBar
import com.example.opencode_mobile.ui.components.DiffCard
import com.example.opencode_mobile.ui.components.DiffLine
import com.example.opencode_mobile.ui.components.DiffLineType

@Composable
fun ReviewScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val diffLines = listOf(
        DiffLine(DiffLineType.Deletion, "- // Default behavior: check hostname"),
        DiffLine(DiffLineType.Addition, "+ // Default behavior: check hostname"),
        DiffLine(DiffLineType.Deletion, "-       (window.location.hostname.includes..."),
        DiffLine(DiffLineType.Addition, "+       (browserIsElectron ||"),
        DiffLine(DiffLineType.Addition, "        window.location.hostname.includes(\"de...")
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            DarkAppBar(
                title = "Review",
                onBackClick = onBackClick
            )
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Review Changes",
                color = Color(0xFFE9E1DF), // on-background from DESIGN.md
                fontSize = 32.sp,
                fontWeight = FontWeight.Light
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "1 file changed",
                        color = Color(0xFF998D97), // outline from DESIGN.md
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "+3",
                        color = Color(0xFF4CAF50),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "-2",
                        color = Color(0xFFF44336),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Collapse all",
                    color = Color(0xFF998D97), // outline from DESIGN.md
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { /* No-op POC */ }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF2B2726), thickness = 1.dp) // divider border color from DESIGN.md
            Spacer(modifier = Modifier.height(16.dp))

            // Diff Card (Expanded)
            DiffCard(
                fileName = "web/src/stores/ApiClient.ts",
                additions = 3,
                deletions = 2,
                diffLines = diffLines,
                initialExpanded = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    Opencode_mobileTheme {
        ReviewScreen(
            onBackClick = {}
        )
    }
}

