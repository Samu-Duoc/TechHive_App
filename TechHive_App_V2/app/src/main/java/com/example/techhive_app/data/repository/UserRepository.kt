package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity
import com.example.techhive_app.data.remote.dto.auth.ChangePasswordDto
import com.example.techhive_app.data.remote.dto.auth.LoginRequestDto
import com.example.techhive_app.data.remote.dto.auth.LoginResponseDto
import com.example.techhive_app.data.remote.dto.auth.RecoverPasswordSecureDto
import com.example.techhive_app.data.remote.dto.auth.RegisterRequestDto
import com.example.techhive_app.data.remote.dto.auth.UpdateProfileDto
import com.example.techhive_app.data.remote.dto.auth.UsuarioDTO
import com.example.techhive_app.data.remote.retrofit.AuthApi
import retrofit2.HttpException
import java.io.IOException

data class UserProfile(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rut: String,
    val direccion: String,
    val telefono: String,
    val role: String
) {
    val fullName: String get() = "$nombre $apellido".trim()
    val isAdmin: Boolean get() = role.equals("ADMIN", true)
}

class UserRepository(
    private val userDao: UserDao,
    private val authApi: AuthApi
) {

    // LOGIN
    suspend fun login(email: String, password: String): Result<LoginResponseDto> {
        return try {
            val response = authApi.login(LoginRequestDto(email, password))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    // REGISTRO
    suspend fun register(dto: RegisterRequestDto): Result<LoginResponseDto> {
        return try {
            val response = authApi.register(dto)
            Result.success(response)
        } catch (e: Exception) {
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
            val user = users.firstOrNull { it.email.equals(email, ignoreCase = true) }

            if (user != null) {
                val profile = UserProfile(
                    id = user.id,
                    nombre = user.nombre,
                    apellido = user.apellido,
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

    // UPDATE PERFIL (sin password)
    suspend fun updateProfile(id: Long, dto: UpdateProfileDto): Result<UsuarioDTO> {
        return try {
            Result.success(authApi.updateProfile(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    // UPDATE USUARIO COMPLETO (si lo usas)
    suspend fun updateUser(id: Long, dto: RegisterRequestDto): Result<UsuarioDTO> {
        return try {
            Result.success(authApi.updateUser(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    // CAMBIAR CONTRASEÑA
    suspend fun changePassword(id: Long, dto: ChangePasswordDto): Result<String> {
        return try {
            Result.success(authApi.changePassword(id, dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    // RECUPERAR CONTRASEÑA POR PREGUNTA (segura)
    suspend fun recoverPasswordSecure(dto: RecoverPasswordSecureDto): Result<String> {
        return try {
            Result.success(authApi.recoverPasswordSecure(dto))
        } catch (e: Exception) {
            Result.failure(Exception(readableError(e)))
        }
    }

    // ERRORES LEGIBLES
    private fun readableError(e: Throwable): String {
        return when (e) {
            is HttpException -> when (e.code()) {
                400 -> "Datos inválidos. Revisa los campos."
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
