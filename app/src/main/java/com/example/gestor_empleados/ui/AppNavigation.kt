package com.example.gestor_empleados.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestor_empleados.ui.screens.history.HistoryScreen
import com.example.gestor_empleados.ui.screens.home.HomeScreen
import com.example.gestor_empleados.ui.screens.leave.LeaveScreen
import com.example.gestor_empleados.ui.screens.login.LoginScreen

@Composable
fun AppNavigation(
    startDestination: String
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {

        composable("login") {
            LoginScreen(onLoginSuccess = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(
                onNavigateToHistory = {
                    navController.navigate("historial")
                },
                onNavigateToLeave = {
                    navController.navigate("leave_request")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("historial") {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable("leave_request") {
            LeaveScreen(onBack = { navController.popBackStack() })
        }
    }
}