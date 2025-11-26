package com.example.gestor_empleados.viewmodel

import com.example.gestor_empleados.MainDispatcherRule
import com.example.gestor_empleados.data.model.Asistencia
import com.example.gestor_empleados.data.repository.AsistenciaRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HistorialViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mock del repositorio
    private val asistenciaRepo = mockk<AsistenciaRepository>()

    private lateinit var viewModel: HistorialViewModel

    @Test
    fun `cargarHistorial exitoso actualiza estado con lista de asistencias`() = runTest {
        // 1. GIVEN: El repositorio devuelve una lista simulada
        val listaPrueba = listOf(
            Asistencia(userId = "user1", rut = "11111111-1"),
            Asistencia(userId = "user1", rut = "11111111-1")
        )
        coEvery { asistenciaRepo.getHistorialDeAsistencia() } returns Result.success(listaPrueba)

        // 2. WHEN: Inicializamos el ViewModel (el init llama a cargarHistorial automáticamente)
        viewModel = HistorialViewModel(asistenciaRepo)

        // 3. THEN: Verificamos que el estado tenga la lista correcta
        assertEquals(listaPrueba, viewModel.uiState.value.asistencias)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.error)
    }

    @Test
    fun `cargarHistorial fallido actualiza estado con error`() = runTest {
        // 1. GIVEN: El repositorio falla
        val mensajeError = "Error al conectar con Firebase"
        coEvery { asistenciaRepo.getHistorialDeAsistencia() } returns Result.failure(Exception(mensajeError))

        // 2. WHEN
        viewModel = HistorialViewModel(asistenciaRepo)

        // 3. THEN: El estado debe mostrar el error y la lista vacía
        assertEquals(mensajeError, viewModel.uiState.value.error)
        assertEquals(emptyList<Asistencia>(), viewModel.uiState.value.asistencias)
        assertFalse(viewModel.uiState.value.isLoading)
    }
}