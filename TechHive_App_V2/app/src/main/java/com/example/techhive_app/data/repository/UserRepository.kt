package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity
import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.retrofit.AuthApi

data class UserProfile(
    val fullName: String,
    val email: String,
    val rut: String,
    val direccion: String,
    val telefono: String,
    val isAdmin: Boolean
)

class UserRepository(
    private val userDao: UserDao,
    private val authApi: AuthApi
) {

    // LOGIN  (AuthApi)
    suspend fun login(email: String, password: String): Result<LoginResponseDto> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // REGISTRO (AuthApi)
    suspend fun register(dto: RegisterRequestDto): Result<LoginResponseDto> {
        return try {
            val response = authApi.register(dto)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // GUARDAR USUARIO EN LOCAL (si lo sigues usando)
    suspend fun saveLocalUser(entity: UserEntity) {
        userDao.insert(entity)
    }

    // PERFIL DESDE MICROSERVICIO
    suspend fun getUserProfileFromMs(email: String): Result<UserProfile> {
        return try {
            val users = authApi.getAllUsers()
            val user = users.firstOrNull { it.email == email }


            if (user != null) {
                val profile = UserProfile(
                    fullName = "${user.nombre} ${user.apellido}",
                    email = user.email,
                    rut = user.rut,
                    direccion = user.direccion,
                    telefono = user.telefono,
                    isAdmin = user.rol == "ADMIN"
                )
                Result.success(profile)
            } else {
                Result.failure(Exception("Usuario no encontrado en MS"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
