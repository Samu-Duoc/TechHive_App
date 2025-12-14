package com.example.techhive_app.data.remote.dto.auth

data class SetSecurityQADto(
    val pregunta: String,
    val respuesta: String,
    val currentPassword: String
)