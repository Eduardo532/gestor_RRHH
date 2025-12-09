package com.example.gestor_empleados

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.gestor_empleados.data.repository.AuthRepository
import com.example.gestor_empleados.ui.AppNavigation
import com.example.gestor_empleados.ui.theme.EmployeeManagerTheme
import com.example.gestor_empleados.utils.BiometricAuthManager
import com.example.gestor_empleados.utils.FileLogger

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileLogger.logEvent(this, "SYSTEM", "Application Started")

        val authRepository = AuthRepository()

        setContent {
            EmployeeManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var destination by remember { mutableStateOf<String?>(null) }
                    var showAuthError by remember { mutableStateOf(false) }
                    var authTrigger by remember { mutableIntStateOf(0) }

                    val biometricAuth = remember { BiometricAuthManager(this@MainActivity) }

                    LaunchedEffect(authTrigger) {
                        showAuthError = false

                        val currentUser = authRepository.getCurrentUserId()

                        if (currentUser == null) {
                            destination = "login"
                        } else {
                            biometricAuth.showBiometricPrompt(
                                title = "Acceso Seguro",
                                subtitle = "Confirme su huella para ingresar",
                                onSuccess = {
                                    FileLogger.logEvent(this@MainActivity, "AUTH", "Biometric success for user: $currentUser")
                                    destination = "home"
                                },
                                onError = {
                                    FileLogger.logEvent(this@MainActivity, "AUTH", "Biometric failed or cancelled")
                                    showAuthError = true
                                }
                            )
                        }
                    }

                    if (destination != null) {
                        AppNavigation(startDestination = destination!!)
                    }
                    else if (showAuthError) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = "No se pudo autenticar",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Es necesaria la huella digital para acceder.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.height(32.dp))

                            Button(
                                onClick = { authTrigger++ },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Reintentar Huella")
                            }

                            Spacer(Modifier.height(16.dp))

                            TextButton(
                                onClick = { destination = "login" }
                            ) {
                                Text("Ingresar con Contraseña")
                            }
                        }
                    }
                    else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}