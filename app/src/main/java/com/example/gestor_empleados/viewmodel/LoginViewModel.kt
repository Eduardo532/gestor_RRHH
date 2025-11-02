package com.example.gestor_empleados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val loginExitoso: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(rut: String, contrasena: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val rutLimpio = rut.replace(".", "").replace(" ", "")

        val emailFalso = "$rutLimpio@gestor.app"

        viewModelScope.launch {
            val resultado = authRepository.login(emailFalso, contrasena)

            resultado.onSuccess {
                _uiState.update { it.copy(isLoading = false, loginExitoso = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "RUT o contraseña incorrectos."
                    )
                }
            }
        }
    }
}