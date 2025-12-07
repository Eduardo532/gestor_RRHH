package com.example.gestor_empleados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.Attendance
import com.example.gestor_empleados.data.repository.AttendanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistoryUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val attendances: List<Attendance> = emptyList()
)

class HistoryViewModel @JvmOverloads constructor(
    private val attendanceRepository: AttendanceRepository = AttendanceRepository()
): ViewModel() {


    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = attendanceRepository.getAttendanceHistory()

            result.onSuccess { attendanceList ->
                _uiState.update {
                    it.copy(isLoading = false, attendances = attendanceList)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.message)
                }
            }
        }
    }
}