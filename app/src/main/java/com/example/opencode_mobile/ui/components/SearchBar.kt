package com.example.opencode_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    placeholder: String,
    onQueryChange: (String) -> Unit = {},
    onSortToggle: () -> Unit = {},
    sortAscending: Boolean = false,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .background(Color(0xFF2B2726), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = Color(0xFF998D97),
                modifier = Modifier.padding(end = 8.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (text.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color(0xFF998D97),
                        fontSize = 16.sp
                    )
                }
                BasicTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onQueryChange(it)
                    },
                    textStyle = TextStyle(color = Color(0xFFE9E1DF), fontSize = 16.sp),
                    cursorBrush = SolidColor(Color(0xFFEDB2F1)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .background(Color(0xFF383433), RoundedCornerShape(8.dp))
                .clickable { onSortToggle() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (sortAscending) "Oldest" else "Recent",
                    color = Color(0xFFE9E1DF),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (sortAscending) Icons.AutoMirrored.Filled.KeyboardArrowRight
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Sort order",
                    tint = Color(0xFFE9E1DF),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
