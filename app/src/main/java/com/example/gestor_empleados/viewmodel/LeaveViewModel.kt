package com.example.gestor_empleados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.repository.LeaveRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaveUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class LeaveViewModel @JvmOverloads constructor(
    private val leaveRepository: LeaveRepository = LeaveRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState = _uiState.asStateFlow()

    fun submitLeaveRequest(reason: String) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, escriba el motivo de la licencia.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }

            val result = leaveRepository.requestLeave(reason)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al enviar: ${exception.message}")
                }
            }
        }
    }

    fun resetState() {
        _uiState.update { LeaveUiState() }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}