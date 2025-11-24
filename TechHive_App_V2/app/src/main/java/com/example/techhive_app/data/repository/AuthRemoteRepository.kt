package com.example.techhive_app.data.repository

import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.remote.retrofit.AuthApi

class AuthRemoteRepository(
    private val api: AuthApi
) {
    suspend fun login(email: String, password: String): LoginResponseDto {
        val body = LoginRequestDto(email = email, password = password)
        return api.login(body)
    }

    suspend fun register(dto: RegisterRequestDto): LoginResponseDto {
        return api.register(dto)
    }

    suspend fun getAllUsers(): List<UsuarioDTO> {
        return api.getAllUsers()
    }
}
