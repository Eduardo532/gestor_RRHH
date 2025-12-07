package com.example.gestor_empleados.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.repository.AuthRepository
import com.example.gestor_empleados.utils.FileLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val error: String? = null
)

class LoginViewModel @JvmOverloads constructor(
    application: Application,
    private val authRepository: AuthRepository = AuthRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(rut: String, pass: String) {
        if (rut.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(error = "Complete todos los campos") }
            return
        }

        val email = if (rut.contains("@")) rut else "$rut@gestor.app"

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            FileLogger.logEvent(getApplication(), "AUTH", "Login attempt for user: $email")

            val result = authRepository.login(email, pass)

            result.onSuccess {
                FileLogger.logEvent(getApplication(), "AUTH", "Login successful for user: $email")
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            }.onFailure { exception ->
                FileLogger.logEvent(getApplication(), "ERROR", "Login failed for $email: ${exception.message}")
                _uiState.update { it.copy(isLoading = false, error = "Error: ${exception.message}") }
            }
        }
    }
}