package com.example.gestor_empleados.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.Attendance
import com.example.gestor_empleados.data.repository.AttendanceRepository
import com.example.gestor_empleados.data.repository.AuthRepository
import com.example.gestor_empleados.utils.LocationManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAttendanceMarked: Boolean = false
)

class HomeViewModel @JvmOverloads constructor(
    application: Application,
    private val locationManager: LocationManager = LocationManager(application),
    private val authRepo: AuthRepository = AuthRepository(),
    private val attendanceRepo: AttendanceRepository = AttendanceRepository()

) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun markAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isAttendanceMarked = false) }

            // Obtener Ubicación
            val location = locationManager.getCurrentLocation()
            if (location == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo obtener la ubicación. Revise permisos y GPS.") }
                return@launch
            }

            // Guardar el registro
            val userId = authRepo.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Error de usuario. Vuelva a iniciar sesión.") }
                return@launch
            }

            val userRut = authRepo.getCurrentUserEmail()?.split("@")?.get(0) ?: "SIN_RUT"

            val newChekIn = Attendance(
                userId = userId,
                rut = userRut,
                timestamp = Timestamp.now(),
                latitude = location.latitude,
                longitude = location.longitude
            )

            val result = attendanceRepo.registerAttendance(newChekIn)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isAttendanceMarked = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al guardar: ${exception.message}")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}