package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity
import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.retrofit.AuthApi
import com.example.techhive_app.data.remote.dto.auth.ChangePasswordDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto
import retrofit2.HttpException
import java.io.IOException

data class UserProfile(
    val id: Long,
    val fullName: String,
    val email: String,
    val rut: String,
    val direccion: String,
    val telefono: String,
    val role: String
) {
    val isAdmin: Boolean get() = role.equals("ADMIN", true)
}


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
            // Aquí va el nuevo bloque catch
            Result.failure(Exception(readableError(e)))
        }
    }

    // REGISTRO (AuthApi)
    suspend fun register(dto: RegisterRequestDto): Result<LoginResponseDto> {
        return try {
            val response = authApi.register(dto)
            Result.success(response)
        } catch (e: Exception) {
            // Aquí también
            Result.failure(Exception(readableError(e)))
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
                    id = user.id,
                    fullName = "${user.nombre} ${user.apellido}",
                    email = user.email,
                    rut = user.rut,
                    direccion = user.direccion,
                    telefono = user.telefono,
                    role = user.rol
                )
                Result.success(profile)
            } else {
                Result.failure(Exception("Usuario no encontrado en MS"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    suspend fun updateProfile(id: Long, dto: UpdateProfileDto): Result<UsuarioDTO> {
        return try {
            Result.success(authApi.updateProfile(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }
    suspend fun updateUser(id: Long, dto: RegisterRequestDto): Result<UsuarioDTO> {
        return try {
            Result.success(authApi.updateUser(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    suspend fun changePassword(id: Long, dto: ChangePasswordDto): Result<String> {
        return try {
            Result.success(authApi.changePassword(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    //VISUALIZA EL ERRORES DEL MS
    private fun readableError(e: Throwable): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                400 -> "Datos inválidos. Revisa email y contraseña."
                401 -> "Credenciales incorrectas."
                404 -> "Servicio no disponible (ruta no encontrada)."
                409 -> "Este email ya está registrado."
                500 -> "Error del servidor. Intenta nuevamente."
                else -> "Error HTTP ${e.code()}."
            }
            is IOException -> "Sin conexión. Revisa tu internet o el servidor."
            else -> e.message ?: "Error desconocido."
        }
    }
}
