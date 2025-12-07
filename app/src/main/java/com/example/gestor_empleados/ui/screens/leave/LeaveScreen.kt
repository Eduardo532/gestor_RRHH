package com.example.gestor_empleados.ui.screens.leave

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestor_empleados.data.model.LeaveRequest
import com.example.gestor_empleados.viewmodel.LeaveViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaveScreen(
    onBack: () -> Unit,
    viewModel: LeaveViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var reasonText by remember { mutableStateOf("") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
            reasonText = ""
            viewModel.resetState()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Solicitudes") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Nueva Solicitud",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = reasonText,
                onValueChange = { reasonText = it },
                label = { Text("Motivo (Ej: Médico, Personal...)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                maxLines = 3,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.submitLeaveRequest(reasonText) },
                modifier = Modifier.fillMaxWidth(),
                enabled = reasonText.isNotBlank() && !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("ENVIAR SOLICITUD")
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            Text(
                text = "Historial de Estados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.leaves.isEmpty() && !uiState.isLoading) {
                Text(
                    text = "No hay solicitudes registradas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.leaves) { leave ->
                        LeaveStatusItem(leave)
                    }
                }
            }
        }
    }
}

@Composable
fun LeaveStatusItem(leave: LeaveRequest) {
    val (backgroundColor, contentColor) = when (leave.status) {
        "Aprobada" -> Pair(Color(0xFF4CAF50), Color.White)
        "Rechazada" -> Pair(Color(0xFFF44336), Color.White)
        else -> Pair(Color(0xFFFFC107), Color.Black)
    }

    val statusText = leave.status.ifEmpty { "Pendiente" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = leave.reason,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatTimestamp(leave.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

private fun formatTimestamp(timestamp: Timestamp): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(timestamp.toDate())
}