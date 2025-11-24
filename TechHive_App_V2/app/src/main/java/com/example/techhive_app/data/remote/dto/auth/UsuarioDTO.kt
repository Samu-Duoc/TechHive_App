package com.example.techhive_app.data.remote.dto.auth

data class UsuarioDTO(
    val id: Long,
    val nombre: String,
    val apellido: String,
    val rut: String,
    val email: String,
    val telefono: String,
    val direccion: String,
    val rol: String,
    val estado: String,
    val fechaRegistro: String
)

