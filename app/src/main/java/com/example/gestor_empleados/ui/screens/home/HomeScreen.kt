package com.example.gestor_empleados.ui.screens.home

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gestor_empleados.utils.BiometricAuthManager
import com.example.gestor_empleados.utils.LocationManager
import com.example.gestor_empleados.viewmodel.HomeViewModel
import com.example.gestor_empleados.viewmodel.HomeUiState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(),
               onNavigateToHistory: () -> Unit) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current


    val biometricAuthManager = BiometricAuthManager(context as FragmentActivity)

    val locationManager = LocationManager(context)

    val onBiometricSuccess = {
        viewModel.markAttendance()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {

            biometricAuthManager.showBiometricPrompt(
                title = "Verificar Asistencia",
                subtitle = "Confirme su identidad para marcar",
                onSuccess = onBiometricSuccess,
                onError = { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "Se requiere permiso de ubicación para marcar", Toast.LENGTH_LONG).show()
        }
    }


    HandleUiState(uiState, viewModel)

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(onClick = onNavigateToHistory) {
            Icon(Icons.Default.List, contentDescription = "Ver Historial")
            Spacer(Modifier.width(8.dp))
            Text("Ver Mi Historial")
        }

        Spacer(Modifier.height(32.dp))
        Text("Control de Asistencia", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Presione el botón para marcar su entrada o salida.")
        Spacer(Modifier.height(48.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(80.dp))
        } else {
            Button(
                onClick = {
                    if (locationManager.hasLocationPermission()) {
                        biometricAuthManager.showBiometricPrompt(
                            title = "Verificar Asistencia",
                            subtitle = "Confirme su identidad para marcar",
                            onSuccess = onBiometricSuccess,
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                Text("Marcar", fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun HandleUiState(uiState: HomeUiState, viewModel: HomeViewModel) {
    val context = LocalContext.current

    LaunchedEffect(uiState.isAttendanceMarked) {
        if (uiState.isAttendanceMarked) {
            Toast.makeText(context, "Asistencia registrada con éxito", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }
}