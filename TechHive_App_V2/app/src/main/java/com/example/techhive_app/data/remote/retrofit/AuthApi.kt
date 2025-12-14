package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.remote.dto.auth.ChangePasswordDto
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto
import com.example.techhive_app.data.remote.dto.auth.RecoverPasswordSecureDto
import com.example.techhive_app.data.remote.dto.auth.SetSecurityQADto
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

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

    // (A) Update completo (ADMIN / mantenimiento)
    @PUT("usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body dto: RegisterRequestDto
    ): UsuarioDTO

    // (B) Update perfil con verificación de contraseña (CLIENTE/USUARIO)
    @PUT("usuarios/{id}/perfil")
    suspend fun updateProfile(
        @Path("id") id: Long,
        @Body dto: UpdateProfileDto
    ): UsuarioDTO

    @PUT("usuarios/{id}/password")
    suspend fun changePassword(
        @Path("id") id: Long,
        @Body dto: ChangePasswordDto
    ): String

    @PUT("usuarios/{id}/security")
    suspend fun setSecurity(@Path("id") id: Long, @Body dto: SetSecurityQADto): String

    @POST("auth/recuperar-clave-segura")
    suspend fun recoverPasswordSecure(
        @Body dto: RecoverPasswordSecureDto
    ): String

}
