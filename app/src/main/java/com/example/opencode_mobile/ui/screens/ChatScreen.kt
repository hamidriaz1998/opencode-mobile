package com.example.opencode_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.opencode_mobile.ui.theme.Opencode_mobileTheme
import com.example.opencode_mobile.ui.components.CodeBadgeText
import com.example.opencode_mobile.ui.components.CommandWidget
import com.example.opencode_mobile.ui.components.DarkAppBar
import com.example.opencode_mobile.ui.components.DiffCard
import com.example.opencode_mobile.ui.components.DiffLine

@Composable
fun ChatScreen(
    sessionId: String,
    onReviewClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            DarkAppBar(
                title = sessionId.ifEmpty { "Chat UI" },
                onBackClick = onBackClick,
                actions = {
                    // "Review" Button
                    Button(
                        onClick = onReviewClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEDB2F1), // primary from DESIGN.md
                            contentColor = Color(0xFF64336C) // text color from DESIGN.md
                        ),
                        shape = RoundedCornerShape(100.dp), // full pill shape
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(text = "Review", fontSize = 14.sp)
                    }
                    
                    IconButton(onClick = { /* No-op POC */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            // Chat Input Panel
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F0E0D)) // Dark footer background
                    .padding(16.dp)
            ) {
                // Input TextField mockup
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161413), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Type a message...",
                        color = Color(0xFF8A8886),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Big Pickle" Dropdown Chip
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF1E1C1A), RoundedCornerShape(16.dp))
                            .clickable { /* No-op */ }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Big Pickle",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Dropdown",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Send Button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFF262423), CircleShape)
                            .clickable { /* No-op */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Send",
                            tint = Color(0xFFC7C5C3)
                        )
                    }
                }
            }
        },
        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            
            CodeBadgeText(
                text = "The typecheck passed for web and electron packages. There's an unrelated error in the mobile package about `expo-clipboard`. That's not related to my change."
            )
            Spacer(modifier = Modifier.height(16.dp))

            CodeBadgeText(
                text = "Let me also run lint specifically for the web package to make sure the code is correct."
            )
            Spacer(modifier = Modifier.height(16.dp))

            CodeBadgeText(
                text = "Typecheck passed for web and electron (the mobile error is unrelated). Let me run lint:"
            )
            Spacer(modifier = Modifier.height(8.dp))

            CommandWidget(command = "bash Lint web package")
            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = Color(0xFF2B2726), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Diff Card (Collapsed inside Chat)
            DiffCard(
                fileName = "web/src/stores/ApiClient.ts",
                additions = 3,
                deletions = 2,
                diffLines = emptyList(),
                initialExpanded = false
            )
            Spacer(modifier = Modifier.height(16.dp))

            CodeBadgeText(
                text = "No errors or new warnings related to my change. The existing warnings are in other files."
            )
            Spacer(modifier = Modifier.height(16.dp))

            CodeBadgeText(
                text = "Let me verify my change looks correct by reading the file again:"
            )
            Spacer(modifier = Modifier.height(16.dp))

            CodeBadgeText(
                text = "No lint errors in `ApiClient.ts`. The fix adds `browserIsElectron` to the `isLocalhost`"
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    Opencode_mobileTheme {
        ChatScreen(
            sessionId = "Electron app login page display issue",
            onReviewClick = {},
            onBackClick = {}
        )
    }
}

