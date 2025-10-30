package com.example.gestor_empleados.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestor_empleados.ui.screens.login.LoginScreen

// Pantalla de Home temporal
@Composable
fun HomeScreen() {
    // Aquí construirás la pantalla principal más adelante
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    // Limpiamos el historial para que el usuario no pueda volver al login con el botón de atrás
                    popUpTo("login") { inclusive = true }
                }
            })
        }
        composable("home") {
            HomeScreen() // Tu pantalla principal
        }
        // Aquí añadirás más composable() para las otras pantallas
    }
}