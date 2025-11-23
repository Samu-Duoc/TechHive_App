package com.example.techhive_app.data.remote.dto

data class RegisterRequestDto(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val password: String,
    val telefono: String,
    val direccion: String
)