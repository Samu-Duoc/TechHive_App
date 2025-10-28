package com.example.techhive_app.data.local.user

import androidx.room.Dao                       // Marca esta interfaz como DAO de Room
import androidx.room.Insert                    // Para insertar filas
import androidx.room.OnConflictStrategy        // Estrategia de conflicto en inserción
import androidx.room.Query                     // Para queries SQL

// operaciones para la tabla users
@Dao
interface UserDao {

    // Inserta un usuario. ABORT si hay conflicto de PK
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // Devuelve un usuario por email o null si no existe
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    // Cuenta total de usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // Lista completa
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>
}