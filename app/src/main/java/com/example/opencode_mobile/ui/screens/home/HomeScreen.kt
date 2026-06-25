package com.example.opencode_mobile.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.opencode_mobile.data.local.Connection
import com.example.opencode_mobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onConnect: (Connection) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var editingConnection by remember { mutableStateOf<Connection?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Connections") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TopBarBg,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Connection")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.connections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No connections yet.\nTap + to add a server.",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(uiState.connections, key = { it.id }) { connection ->
                    ConnectionCard(
                        connection = connection,
                        isActive = connection.id == uiState.activeConnectionId,
                        onClick = { onConnect(connection) },
                        onEdit = { editingConnection = it },
                        onDelete = { viewModel.deleteConnection(it) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        ConnectionDialog(
            title = "Add Connection",
            initial = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, address, port, password ->
                viewModel.addConnection(name, address, port, password)
                showAddDialog = false
            }
        )
    }

    editingConnection?.let { conn ->
        ConnectionDialog(
            title = "Edit Connection",
            initial = conn,
            onDismiss = { editingConnection = null },
            onSave = { name, address, port, password ->
                viewModel.updateConnection(conn.id, name, address, port, password)
                editingConnection = null
            }
        )
    }
}

@Composable
private fun ConnectionCard(
    connection: Connection,
    isActive: Boolean,
    onClick: () -> Unit,
    onEdit: (Connection) -> Unit,
    onDelete: (Connection) -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) MaterialTheme.colorScheme.surfaceContainer else CardBg
        ),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Cable,
                contentDescription = null,
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = connection.name.ifBlank { "${connection.address}:${connection.port}" },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp
                )
                Text(
                    text = "${connection.address}:${connection.port}",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 13.sp
                )
                if (isActive) {
                    Text(
                        text = "Connected",
                        color = SuccessGreen,
                        fontSize = 12.sp
                    )
                }
            }
            IconButton(onClick = { onEdit(connection) }) {
                Icon(Icons.Default.Edit, "Edit", tint = MaterialTheme.colorScheme.outline)
            }
            IconButton(onClick = { onDelete(connection) }) {
                Icon(Icons.Default.Delete, "Delete", tint = ErrorRed)
            }
        }
    }
}

@Composable
private fun ConnectionDialog(
    title: String,
    initial: Connection?,
    onDismiss: () -> Unit,
    onSave: (name: String, address: String, port: Int, password: String) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var address by remember { mutableStateOf(initial?.address ?: "") }
    var port by remember { mutableStateOf((initial?.port ?: 4096).toString()) }
    var password by remember { mutableStateOf(initial?.password ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Divider,
        title = {
            Text(title, color = MaterialTheme.colorScheme.onSurface)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name (optional)") },
                    singleLine = true,
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address *") },
                    placeholder = { Text("192.168.1.100 or example.com") },
                    singleLine = true,
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = port,
                    onValueChange = { port = it.filter { c -> c.isDigit() } },
                    label = { Text("Port") },
                    singleLine = true,
                    colors = textFieldColors()
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (optional)") },
                    singleLine = true,
                    colors = textFieldColors()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (address.isNotBlank()) {
                        val portNum = port.toIntOrNull() ?: 4096
                        onSave(name, address, portNum, password)
                    }
                },
                enabled = address.isNotBlank()
            ) {
                Text("Save", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.outline)
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.outline,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
)
