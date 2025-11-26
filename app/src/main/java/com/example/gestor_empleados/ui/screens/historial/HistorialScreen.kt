package com.example.gestor_empleados.ui.screens.historial

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestor_empleados.data.model.Asistencia
import com.example.gestor_empleados.viewmodel.HistorialViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

// Función principal de la pantalla
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    onBack: () -> Unit,
    viewModel: HistorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Historial") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
                uiState.asistencias.isEmpty() -> {
                    Text("No tienes marcajes registrados.")
                }
                else -> {
                    // Lista de asistencias
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(uiState.asistencias) { asistencia ->
                            AsistenciaItem(asistencia)
                        }
                    }
                }
            }
        }
    }
}

// Composable para mostrar cada fila de la lista
@Composable
fun AsistenciaItem(asistencia: Asistencia) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = formatTimestamp(asistencia.timestamp, "EEEE dd 'de' MMMM"), // Ej: "Jueves 07 de Noviembre"
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "RUT: ${asistencia.rut}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = formatTimestamp(asistencia.timestamp, "HH:mm 'hrs'"), // Ej: "09:30 hrs"
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// Función "helper" para convertir la fecha de Firebase a texto legible
private fun formatTimestamp(timestamp: Timestamp, pattern: String): String {
    val sdf = SimpleDateFormat(pattern, Locale("es", "ES"))
    return sdf.format(timestamp.toDate())
}