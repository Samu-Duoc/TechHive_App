package com.example.techhive_app.data.remote.dto.auth

data class RecoverPasswordSecureDto(
    val email: String,
    val respuesta: String,
    val nuevaPassword: String
)
