package com.example.opencode_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CodeBadgeText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Color(0xFFA09E9C) // Muted light gray from the screenshots
) {
    val parts = text.split("`")
    
    FlowRow(
        modifier = modifier,
        maxItemsInEachRow = Int.MAX_VALUE
    ) {
        parts.forEachIndexed { index, part ->
            if (part.isEmpty()) return@forEachIndexed
            
            if (index % 2 == 1) {
                // Inline Code Badge
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .background(Color(0xFF1E1C1A), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = part,
                        color = Color(0xFFEFB8C8), // Light pink/lavender text for code
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            } else {
                // Standard Text segment
                Text(
                    text = part,
                    color = textColor,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}
