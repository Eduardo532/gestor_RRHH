package com.example.gestor_empleados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val isloginSuccessful: Boolean = false,
    val error: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun login(rut: String, password: String) {
        _uiState.update { it.copy(isLoading = true, error = null) }

        val cleanRut = rut.replace(".", "").replace(" ", "")

        val fakeEmail = "$cleanRut@gestor.app"

        viewModelScope.launch {
            val result = authRepository.login(fakeEmail, password)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isloginSuccessful = true) }
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