package com.example.gestor_empleados.viewmodel

import android.app.Application
import android.location.Location
import com.example.gestor_empleados.MainDispatcherRule
import com.example.gestor_empleados.data.repository.AttendanceRepository
import com.example.gestor_empleados.data.repository.AuthRepository
import com.example.gestor_empleados.utils.LocationManager
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val application = mockk<Application>(relaxed = true)
    private val locationManager = mockk<LocationManager>()
    private val authRepo = mockk<AuthRepository>()
    private val asistenciaRepo = mockk<AttendanceRepository>()

    private lateinit var viewModel: HomeViewModel

    @Test
    fun `marcarAsistencia con todo correcto guarda en base de datos`() = runTest {
        // 1. GIVEN
        // Simulamos una ubicación válida
        val mockLocation = mockk<Location>()
        every { mockLocation.latitude } returns -38.0
        every { mockLocation.longitude } returns -72.0
        coEvery { locationManager.getCurrentLocation() } returns mockLocation

        // Simulamos usuario logueado
        every { authRepo.getCurrentUserId() } returns "user123"
        every { authRepo.getCurrentUserEmail() } returns "11111111-1@gestor.app"

        // Simulamos que guardar en Firestore funciona
        coEvery { asistenciaRepo.registerAttendance(any()) } returns Result.success(Unit)

        // Inyectamos los mocks (Necesitas ajustar tu ViewModel para esto*)
        viewModel = HomeViewModel(application, locationManager, authRepo, asistenciaRepo)

        // 2. WHEN
        viewModel.markAttendance()

        // 3. THEN
        assertTrue(viewModel.uiState.value.isAttendanceMarked)
    }
}