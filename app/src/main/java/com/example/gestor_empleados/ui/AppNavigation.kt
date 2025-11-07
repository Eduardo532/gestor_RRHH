package com.example.gestor_empleados.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestor_empleados.ui.screens.historial.HistorialScreen
import com.example.gestor_empleados.ui.screens.home.HomeScreen
import com.example.gestor_empleados.ui.screens.login.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(
                // 1. Añadimos la acción para navegar al historial
                onNavigateToHistorial = {
                    navController.navigate("historial")
                }
            )
        }

        // --- 2. AÑADE ESTA NUEVA RUTA ---
        composable("historial") {
            HistorialScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}