package com.example.gestor_empleados.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.LeaveRequest
import com.example.gestor_empleados.data.repository.LeaveRepository
import com.example.gestor_empleados.utils.FileLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LeaveUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val leaves: List<LeaveRequest> = emptyList()
)

class LeaveViewModel @JvmOverloads constructor(
    application: Application,
    private val leaveRepository: LeaveRepository = LeaveRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LeaveUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLeaves()
    }

    fun loadLeaves() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = leaveRepository.getUserLeaves()

            result.onSuccess { list ->
                _uiState.update { it.copy(isLoading = false, leaves = list) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar: ${e.message}") }
            }
        }
    }

    fun submitLeaveRequest(reason: String) {
        if (reason.isBlank()) {
            _uiState.update { it.copy(error = "Por favor, escriba el motivo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isSuccess = false) }
            val result = leaveRepository.requestLeave(reason)

            result.onSuccess {
                FileLogger.logEvent(getApplication(), "TRANSACTION", "New leave request submitted: $reason")
                loadLeaves()
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exception ->
                FileLogger.logEvent(getApplication(), "ERROR", "Leave request failed: ${exception.message}")
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, error = null, isLoading = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}