package com.example.techhive_app.data.remote.dto.auth
data class RegisterRequestDto(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val password: String,
    val telefono: String,
    val direccion: String,
    val preguntaSeguridad: String? = null,   // opcional; si null/blank, backend usa la default
    val respuestaSeguridad: String? = null
)

typealias UpdateUserDto = RegisterRequestDto
