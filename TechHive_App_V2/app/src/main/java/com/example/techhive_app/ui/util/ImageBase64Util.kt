package com.example.techhive_app.ui.util

fun toDataImage(base64: String?): String? {
    if (base64.isNullOrBlank()) return null
    // Si ya viene con prefijo, no lo dupliques
    return if (base64.startsWith("data:image")) base64
    else "data:image/jpeg;base64,$base64"
}
