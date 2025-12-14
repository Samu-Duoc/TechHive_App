package com.example.techhive_app.data.repository

import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.remote.retrofit.AuthApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRemoteRepositoryTest {

    // Test de login: 1. Verifica que el repositorio construye correctamente el LoginRequestDto
    //                2. Verifica que se llama al endpoint login del AuthApi
    //                3. Verifica que se retorna el usuario entregado por la API
    @Test
    fun login_envia_body_correcto_y_retorna_usuario() = runBlocking {
        val api = mockk<AuthApi>()
        val repo = AuthRemoteRepository(api)

        val slotBody = slot<LoginRequestDto>()

        val response = LoginResponseDto(
            id = 1L,
            nombre = "Admin",
            apellido = "Root",
            email = "admin@correo.com",
            rut = "11111111-1",
            telefono = "912345678",
            direccion = "Admin St",
            rol = "ADMIN",
            estado = "ACTIVO",
            fechaRegistro = "2025-01-01"
        )

        // Definimos el comportamiento del mock
        coEvery { api.login(capture(slotBody)) } returns response

        // Ejecutamos la lógica a probar
        val result = repo.login("admin@correo.com", "1234")

        // Validamos que retorna el usuario esperado
        assertEquals(1L, result.id)
        assertEquals("ADMIN", result.rol)
        assertEquals("admin@correo.com", result.email)

        // Validamos que el body enviado es el correcto
        assertEquals("admin@correo.com", slotBody.captured.email)
        assertEquals("1234", slotBody.captured.password)

        // Confirmamos que el endpoint se llamó una sola vez
        coVerify(exactly = 1) { api.login(any()) }
    }
    // test de Registro: 1. Verifica que el repositorio construye correctamente el RegisterRequestDto
    //                   2. Verifica que se llama al endpoint register del AuthApi
    //                   3. Verifica que se retorna el usuario entregado por la API

    @Test
    fun register_retorna_usuario_registrado() = runBlocking {
        val api = mockk<AuthApi>()
        val repo = AuthRemoteRepository(api)

        val dto = RegisterRequestDto(
            nombre = "Samuel",
            apellido = "Fuenzalida",
            rut = "12345678K",
            email = "samuel@gmail.com",
            password = "Aa1!aaaa",
            telefono = "912345678",
            direccion = "Mi casa 123"
        )

        val response = LoginResponseDto(
            id = 2L,
            nombre = "Samuel",
            apellido = "Fuenzalida",
            email = "samuel@gmail.com",
            rut = "12345678K",
            telefono = "912345678",
            direccion = "Mi casa 123",
            rol = "CLIENTE",
            estado = "ACTIVO",
            fechaRegistro = "2025-01-02"
        )

        coEvery { api.register(dto) } returns response

        val result = repo.register(dto)

        assertEquals(2L, result.id)
        assertEquals("samuel@gmail.com", result.email)
        assertEquals("CLIENTE", result.rol)

        coVerify(exactly = 1) { api.register(dto) }
    }

    // Test de getAllUsers: 1. Verifica que se llama al endpoint getAllUsers del AuthApi
    //                      2. Verifica que se retorna la lista de usuarios desde la API
    @Test
    fun getAllUsers_retorna_lista_desde_api() = runBlocking {
        val api = mockk<AuthApi>()
        val repo = AuthRemoteRepository(api)

        val users = listOf(
            UsuarioDTO(
                id = 1L,
                nombre = "Admin",
                apellido = "Root",
                rut = "11111111-1",
                email = "admin@correo.com",
                telefono = "912345678",
                direccion = "Admin St",
                rol = "ADMIN",
                estado = "ACTIVO",
                fechaRegistro = "2025-01-01"
            ),
            UsuarioDTO(
                id = 2L,
                nombre = "Samuel",
                apellido = "Fuenzalida",
                rut = "12345678K",
                email = "samuel@gmail.com",
                telefono = "912345678",
                direccion = "Mi casa 123",
                rol = "CLIENTE",
                estado = "ACTIVO",
                fechaRegistro = "2025-01-02"
            )
        )

        coEvery { api.getAllUsers() } returns users

        val result = repo.getAllUsers()

        assertTrue(result.isNotEmpty())
        assertEquals(2, result.size)
        assertEquals("ADMIN", result[0].rol)

        coVerify(exactly = 1) { api.getAllUsers() }
    }
}
