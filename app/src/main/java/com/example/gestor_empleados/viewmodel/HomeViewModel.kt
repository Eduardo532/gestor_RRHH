package com.example.gestor_empleados.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.Attendance
import com.example.gestor_empleados.data.repository.AttendanceRepository
import com.example.gestor_empleados.data.repository.AuthRepository
import com.example.gestor_empleados.data.repository.ConfigRepository
import com.example.gestor_empleados.utils.FileLogger
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
    private val attendanceRepo: AttendanceRepository = AttendanceRepository(),
    private val configRepo: ConfigRepository = ConfigRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun markAttendance() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, isAttendanceMarked = false) }

            val currentLocation = locationManager.getCurrentLocation()
            if (currentLocation == null) {
                FileLogger.logEvent(getApplication(), "ERROR", "Mark Attendance: Location is null")
                _uiState.update { it.copy(isLoading = false, error = "No se pudo obtener la ubicación. Revise permisos y GPS.") }
                return@launch
            }

            val configResult = configRepo.getWorkplaceConfig()

            if (configResult.isFailure) {
                val errorMsg = configResult.exceptionOrNull()?.message ?: "Error de configuración"
                FileLogger.logEvent(getApplication(), "ERROR", "Config Fetch Failed: $errorMsg")
                _uiState.update { it.copy(isLoading = false, error = "Error al obtener configuración de empresa: $errorMsg") }
                return@launch
            }

            val workplaceConfig = configResult.getOrNull()!!

            val workplaceLocation = Location("firebase_provider").apply {
                latitude = workplaceConfig.latitud
                longitude = workplaceConfig.longitud
            }

            val distanceInMeters = currentLocation.distanceTo(workplaceLocation)

            FileLogger.logEvent(getApplication(), "INFO", "Geofence Check: User at $distanceInMeters m from workplace (Max allowed: ${workplaceConfig.radio_permitido} m)")

            if (distanceInMeters > workplaceConfig.radio_permitido) {
                FileLogger.logEvent(getApplication(), "WARNING", "Mark Attendance Denied: Outside dynamic range")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Está fuera del rango permitido (${workplaceConfig.radio_permitido.toInt()}m). Distancia actual: ${distanceInMeters.toInt()}m"
                    )
                }
                return@launch
            }

            val userId = authRepo.getCurrentUserId()
            if (userId == null) {
                FileLogger.logEvent(getApplication(), "ERROR", "Mark Attendance: User ID is null")
                _uiState.update { it.copy(isLoading = false, error = "Error de usuario. Vuelva a iniciar sesión.") }
                return@launch
            }

            val userRut = authRepo.getCurrentUserEmail()?.split("@")?.get(0) ?: "SIN_RUT"

            val newCheckIn = Attendance(
                userId = userId,
                rut = userRut,
                timestamp = Timestamp.now(),
                latitude = currentLocation.latitude,
                longitude = currentLocation.longitude
            )

            val result = attendanceRepo.registerAttendance(newCheckIn)
            result.onSuccess {
                FileLogger.logEvent(getApplication(), "SUCCESS", "Attendance registered for user: $userRut")
                _uiState.update { it.copy(isLoading = false, isAttendanceMarked = true) }
            }.onFailure { exception ->
                FileLogger.logEvent(getApplication(), "ERROR", "Firestore Error: ${exception.message}")
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al guardar: ${exception.message}")
                }
            }
        }
    }

    fun logoutUser() {
        FileLogger.logEvent(getApplication(), "AUTH", "User logged out")
        authRepo.logout()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}