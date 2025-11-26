package com.example.gestor_empleados.viewmodel

import com.example.gestor_empleados.MainDispatcherRule
import com.example.gestor_empleados.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import io.mockk.checkEquals

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // Mocks: Simulamos el repositorio
    private val authRepository = mockk<AuthRepository>()

    // Clase a probar
    private lateinit var viewModel: LoginViewModel

    @Test
    fun `login exitoso actualiza estado a loginExitoso true`() = runTest {
        // 1. GIVEN (Dado que): El repositorio responde éxito
        val rutPrueba = "11.111.111-1"
        val emailEsperado = "11111111-1@gestor.app"
        val password = "password"


        coEvery {
            authRepository.login(eq(emailEsperado), eq(password))
        } returns Result.success(Unit)

        // Inicializamos el ViewModel inyectando el mock (Nota: necesitarás refactorizar el VM para aceptar el repo en el constructor, ver nota abajo*)
        viewModel = LoginViewModel(authRepository)

        // 2. WHEN (Cuando): Llamamos a login
        viewModel.login(rutPrueba,password)

        // 3. THEN (Entonces): El estado debe ser exitoso
        assertTrue(viewModel.uiState.value.loginExitoso)

        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `login fallido actualiza estado con error`() = runTest {
        // 1. GIVEN: El repositorio falla
        coEvery { authRepository.login(any(), any()) } returns Result.failure(Exception("Error de red"))

        viewModel = LoginViewModel(authRepository)

        // 2. WHEN
        viewModel.login("11.111.111-1", "password")

        // 3. THEN
        assertEquals("RUT o contraseña incorrectos.", viewModel.uiState.value.error)
    }
}