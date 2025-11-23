package com.example.techhive_app.data.repository

import com.example.techhive_app.data.remote.dto.LoginRequestDto
import com.example.techhive_app.data.remote.dto.LoginResponseDto
import com.example.techhive_app.data.remote.dto.RegisterRequestDto
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


}