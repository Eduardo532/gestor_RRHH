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

        coEvery { authRepository.login(any(), any()) } returns Result.success(Unit)
        val rutPrueba = "11.111.111-1"
        val emailEsperado = "11111111-1@gestor.app"
        val password = "password"


        coEvery {
            authRepository.login(eq(emailEsperado), eq(password))
        } returns Result.success(Unit)

        viewModel = LoginViewModel(authRepository)

        viewModel.login(rutPrueba,password)


        assertTrue(viewModel.uiState.value.isLoginSuccessful)

        assertEquals(false, viewModel.uiState.value.isLoading)    }
}