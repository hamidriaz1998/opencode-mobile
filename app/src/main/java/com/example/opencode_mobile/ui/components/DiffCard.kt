package com.example.opencode_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.BorderStroke
import com.example.opencode_mobile.ui.theme.*

enum class DiffLineType {
    Normal, Addition, Deletion
}

data class DiffLine(
    val type: DiffLineType,
    val text: String
)

@Composable
fun DiffCard(
    fileName: String,
    additions: Int,
    deletions: Int,
    diffLines: List<DiffLine>,
    initialExpanded: Boolean = false,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(initialExpanded) }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBg), // Level 1 Cards surface color
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Divider) // 1px border #2b2726
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                    contentDescription = "Expand/Collapse",
                    tint = MaterialTheme.colorScheme.outline // outline color
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = fileName,
                    color = MaterialTheme.colorScheme.onSurface, // on-surface color
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "+$additions",
                    color = SuccessGreen, // Green for additions
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "-$deletions",
                    color = ErrorRed, // Red for deletions
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Expanded Diff Code Details
            if (expanded) {
                HorizontalDivider(color = Divider)

                // Show unchanged lines button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest) // lowest container surface background
                        .padding(vertical = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Show unchanged lines (3)",
                        color = MaterialTheme.colorScheme.outline, // outline color
                        fontSize = 12.sp
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                HorizontalDivider(color = Divider)

                // Diff lines
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                ) {
                    diffLines.forEach { line ->
                        val backgroundColor = when (line.type) {
                            DiffLineType.Addition -> DiffAddBg // Dark green
                            DiffLineType.Deletion -> DiffDelBg // Dark red
                            DiffLineType.Normal -> Color.Transparent
                        }
                        
                        val textColor = when (line.type) {
                            DiffLineType.Addition -> DiffAddText // Light green
                            DiffLineType.Deletion -> DiffDelText // Light red
                            DiffLineType.Normal -> MaterialTheme.colorScheme.onSurface // on-surface color
                        }

                        Text(
                            text = line.text,
                            color = textColor,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(backgroundColor)
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
