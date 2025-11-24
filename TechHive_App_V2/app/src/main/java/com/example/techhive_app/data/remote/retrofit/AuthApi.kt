package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import retrofit2.http.Body
import retrofit2.http.POST
import  retrofit2.http.GET

interface AuthApi {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequestDto
    ): LoginResponseDto

    @POST("auth/registro")
    suspend fun register(
        @Body body: RegisterRequestDto
    ): LoginResponseDto

    @GET("usuarios")
    suspend fun getAllUsers(): List<UsuarioDTO>

}
