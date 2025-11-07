package com.example.gestor_empleados.viewmodel

import android.app.Application
//import androidx.fragment.app.FragmentActivity // <-- Ya no se necesita
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestor_empleados.data.model.Asistencia
import com.example.gestor_empleados.data.repository.AsistenciaRepository
import com.example.gestor_empleados.data.repository.AuthRepository
//import com.example.gestor_empleados.utils.BiometricAuth // <-- Ya no se necesita
import com.example.gestor_empleados.utils.LocationManager
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val marcajeExitoso: Boolean = false
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val locationManager = LocationManager(application)
    private val authRepo = AuthRepository()
    private val asistenciaRepo = AsistenciaRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    fun marcarAsistencia() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, marcajeExitoso = false) }

            val location = locationManager.getCurrentLocation()
            if (location == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo obtener la ubicación. Revise permisos y GPS.") }
                return@launch
            }

            val userId = authRepo.getCurrentUserId()
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "Error de usuario. Vuelva a iniciar sesión.") }
                return@launch
            }

            val rutUsuario = authRepo.getCurrentUserEmail()?.split("@")?.get(0) ?: "SIN_RUT"

            val nuevoMarcaje = Asistencia(
                userId = userId,
                rut = rutUsuario,
                timestamp = Timestamp.now(),
                latitud = location.latitude,
                longitud = location.longitude
            )

            val resultado = asistenciaRepo.registrarAsistencia(nuevoMarcaje)
            resultado.onSuccess {
                _uiState.update { it.copy(isLoading = false, marcajeExitoso = true) }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al guardar: ${exception.message}")
                }
            }
        }
    }

    fun errorMostrado() {
        _uiState.update { it.copy(error = null) }
    }
}