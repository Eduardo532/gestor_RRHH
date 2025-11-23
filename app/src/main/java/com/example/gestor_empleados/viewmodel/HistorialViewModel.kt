package com.example.gestor_empleados.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.Asistencia
import com.example.gestor_empleados.data.repository.AsistenciaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HistorialUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val asistencias: List<Asistencia> = emptyList()
)

class HistorialViewModel(
    private val asistenciaRepo: AsistenciaRepository = AsistenciaRepository()
): ViewModel() {


    private val _uiState = MutableStateFlow(HistorialUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarHistorial()
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val resultado = asistenciaRepo.getHistorialDeAsistencia()

            resultado.onSuccess { listaAsistencias ->
                _uiState.update {
                    it.copy(isLoading = false, asistencias = listaAsistencias)
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = exception.message)
                }
            }
        }
    }
}