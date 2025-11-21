package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null && user.password == password) {
            Result.success(user)
        } else {
            Result.failure(IllegalArgumentException("Credenciales inválidas"))
        }
    }

    suspend fun register(name: String, email: String, phone: String, password: String): Result<Long> {
        val exists = userDao.getByEmail(email) != null
        if (exists) {
            return Result.failure(IllegalStateException("El correo ya está registrado"))
        }
        val id = userDao.insert(
            UserEntity(
                name = name,
                email = email,
                phone = phone,
                password = password
            )
        )
        return Result.success(id)
    }

    // función para obtener un usuario por su email
    suspend fun getUserByEmail(email: String): Result<UserEntity> {
        val user = userDao.getByEmail(email)
        return if (user != null) {
            Result.success(user)
        } else {
            Result.failure(NoSuchElementException("Usuario no encontrado con ese email"))
        }
    }
}