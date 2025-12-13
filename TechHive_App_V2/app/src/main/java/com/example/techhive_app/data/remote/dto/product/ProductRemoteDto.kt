package com.example.techhive_app.data.remote.dto.product

data class ProductRemoteDto(
    val id: Long? = null,
    val nombre: String,
    val descripcion: String,
    val stock: Int,
    val precio: Double,
    val estado: String,
    val categoria: String,
    val disponibilidad: String? = null,
    val sku: String,
    val imagenBase64: String? = null
)
