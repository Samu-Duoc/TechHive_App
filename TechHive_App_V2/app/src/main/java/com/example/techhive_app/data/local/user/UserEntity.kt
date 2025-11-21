package com.example.techhive_app.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) // Clave primaria autoincremental
    val id: Long = 0L,

    val name: String, // Nombre
    val email: String, // Correo
    val phone: String, // Teléfono
    val password: String // Contraseña
)