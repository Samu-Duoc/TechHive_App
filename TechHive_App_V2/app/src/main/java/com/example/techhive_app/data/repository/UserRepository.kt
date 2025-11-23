package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity
import com.example.techhive_app.data.remote.dto.LoginRequestDto
import com.example.techhive_app.data.remote.dto.RegisterRequestDto
import com.example.techhive_app.data.remote.retrofit.AuthApi

class UserRepository(
    private val userDao: UserDao,
    private val authApi: AuthApi
) {

    // ---------------- LOGIN contra el microservicio ----------------
    suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            // 1) Llamar al MS
            val body = LoginRequestDto(email = email, password = password)
            val remote = authApi.login(body)

            // 2) Mapear al UserEntity local (Room)
            val localUser = UserEntity(
                name = "${remote.nombre} ${remote.apellido}",
                email = remote.email,
                phone = remote.telefono,
                password = password // si no quieres guardarla, la quitamos luego
            )

            // 3) Guardar en Room (por si quieres usar el perfil local)
            userDao.insert(localUser)

            Result.success(localUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- REGISTRO contra el microservicio ----------------
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<Long> {
        return try {
            // separar nombre y apellido a partir de "Nombre Apellido"
            val parts = name.trim().split(" ", limit = 2)
            val nombre = parts.getOrNull(0) ?: ""
            val apellido = parts.getOrNull(1) ?: ""

            val body = RegisterRequestDto(
                nombre = nombre,
                apellido = apellido,
                rut = "111111111",          // fijo por ahora (tu MS lo exige)
                email = email,
                password = password,
                telefono = phone,
                direccion = "Av.Funciona xfis"  // fijo por ahora
            )

            val remote = authApi.register(body)

            // Guardar también en Room
            val id = userDao.insert(
                UserEntity(
                    name = "$nombre $apellido",
                    email = remote.email,
                    phone = phone,
                    password = password
                )
            )

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ---------------- PERFIL desde la BD local ----------------
    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(NoSuchElementException("Usuario no encontrado con ese email"))
        }
    }
}
