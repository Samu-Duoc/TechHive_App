package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.LoginRequestDto
import com.example.techhive_app.data.remote.dto.LoginResponseDto
import com.example.techhive_app.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): LoginResponseDto

    @POST("auth/registro")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): LoginResponseDto
}
