package com.example.techhive_app.data.remote.dto.auth

data class LoginResponseDto(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rut: String,
    val telefono: String,
    val direccion: String,
    val rol: String,
    val estado: String,
    val fechaRegistro: String
)
