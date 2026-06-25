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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.opencode_mobile.ui.theme.*

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
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onQueryChange(it)
            },
            placeholder = { Text(placeholder) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.outline,
                )
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = Divider,
                unfocusedBorderColor = Divider,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedPlaceholderColor = MaterialTheme.colorScheme.outline,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline,
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(8.dp))
                .clickable { onSortToggle() }
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (sortAscending) "Oldest" else "Recent",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(4.dp))
                Icon(
                    imageVector = if (sortAscending) Icons.AutoMirrored.Filled.KeyboardArrowRight
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Sort order",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
