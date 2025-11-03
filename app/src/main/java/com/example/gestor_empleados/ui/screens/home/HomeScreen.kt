package com.example.gestor_empleados.ui.screens.home

import android.Manifest
import android.content.Context
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
import com.example.gestor_empleados.utils.BiometricAuth
import com.example.gestor_empleados.utils.LocationManager
import com.example.gestor_empleados.viewmodel.HomeViewModel
import com.example.gestor_empleados.viewmodel.HomeUiState

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // --- INICIO DE LA NUEVA LÓGICA ---

    // 1. Helper de Biometría
    val biometricAuth = BiometricAuth(context as FragmentActivity)

    // 2. Helper de Ubicación
    val locationManager = LocationManager(context)

    // 3. Función que se llamará solo si la biometría es exitosa
    val onBiometricSuccess = {
        viewModel.marcarAsistencia()
    }

    // 4. Launcher para pedir permisos de GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            // Permiso concedido, AHORA mostramos la huella
            biometricAuth.showBiometricPrompt(
                title = "Verificar Asistencia",
                subtitle = "Confirme su identidad para marcar",
                onSuccess = onBiometricSuccess,
                onError = { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            // Permiso denegado
            Toast.makeText(context, "Se requiere permiso de ubicación para marcar", Toast.LENGTH_LONG).show()
        }
    }

    // --- FIN DE LA NUEVA LÓGICA ---

    HandleUiState(uiState, viewModel)

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Control de Asistencia", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Presione el botón para marcar su entrada o salida.")
        Spacer(Modifier.height(48.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(80.dp))
        } else {
            Button(
                onClick = {
                    // --- LÓGICA DE CLICK REFACTORIZADA ---
                    if (locationManager.hasLocationPermission()) {
                        // 1. Si ya tenemos permiso, mostramos la huella
                        biometricAuth.showBiometricPrompt(
                            title = "Verificar Asistencia",
                            subtitle = "Confirme su identidad para marcar",
                            onSuccess = onBiometricSuccess,
                            onError = { errorMsg ->
                                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        // 2. Si no tenemos permiso, lo pedimos.
                        // El launcher se encargará de llamar a la huella si el permiso se concede.
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

    LaunchedEffect(uiState.marcajeExitoso) {
        if (uiState.marcajeExitoso) {
            Toast.makeText(context, "Asistencia registrada con éxito", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.errorMostrado()
        }
    }
}