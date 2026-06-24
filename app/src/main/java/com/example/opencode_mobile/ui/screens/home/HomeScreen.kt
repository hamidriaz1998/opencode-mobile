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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.opencode_mobile.data.local.Connection

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
                    containerColor = Color(0xFF0F0E0D),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFEDB2F1),
                contentColor = Color(0xFF64336C),
                shape = RoundedCornerShape(100.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Connection")
            }
        },
        containerColor = Color(0xFF161312)
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
                    color = Color(0xFF998D97),
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
            containerColor = if (isActive) Color(0xFF221F1E) else Color(0xFF141110)
        ),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = if (isActive) 2.dp else 1.dp
        )
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
                tint = if (isActive) Color(0xFFEDB2F1) else Color(0xFF998D97),
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = connection.name.ifBlank { "${connection.address}:${connection.port}" },
                    color = Color(0xFFE9E1DF),
                    fontSize = 16.sp
                )
                Text(
                    text = "${connection.address}:${connection.port}",
                    color = Color(0xFF998D97),
                    fontSize = 13.sp
                )
                if (isActive) {
                    Text(
                        text = "Connected",
                        color = Color(0xFF4CAF50),
                        fontSize = 12.sp
                    )
                }
            }
            IconButton(onClick = { onEdit(connection) }) {
                Icon(Icons.Default.Edit, "Edit", tint = Color(0xFF998D97))
            }
            IconButton(onClick = { onDelete(connection) }) {
                Icon(Icons.Default.Delete, "Delete", tint = Color(0xFFF44336))
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
        containerColor = Color(0xFF2B2726),
        title = {
            Text(title, color = Color.White)
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
                Text("Save", color = Color(0xFFEDB2F1))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color(0xFF998D97))
            }
        }
    )
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = Color(0xFFE9E1DF),
    unfocusedTextColor = Color(0xFFE9E1DF),
    cursorColor = Color(0xFFEDB2F1),
    focusedBorderColor = Color(0xFFEDB2F1),
    unfocusedBorderColor = Color(0xFF4D444D),
    focusedLabelColor = Color(0xFFEDB2F1),
    unfocusedLabelColor = Color(0xFF998D97),
    focusedContainerColor = Color(0xFF1E1B1A),
    unfocusedContainerColor = Color(0xFF1E1B1A)
)
