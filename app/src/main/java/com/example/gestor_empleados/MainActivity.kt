package com.example.gestor_empleados

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.example.gestor_empleados.ui.AppNavigation
import com.example.gestor_empleados.ui.theme.GestorEmpleadosTheme
import com.example.gestor_empleados.utils.BiometricAuth

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GestorEmpleadosTheme {
                var isAuthenticated by remember { mutableStateOf(false) }

                if (isAuthenticated) {
                    AppNavigation()
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Por favor, autentíquese para continuar.")
                    }
                }

                LaunchedEffect(Unit) {
                    BiometricAuth(this@MainActivity).showBiometricPrompt(
                        title = "Autenticación Requerida",
                        subtitle = "Use su huella para abrir la app",
                        onSuccess = {
                            isAuthenticated = true
                        },
                        onError = {
                            finish()
                        }
                    )
                }
            }
        }
    }
}