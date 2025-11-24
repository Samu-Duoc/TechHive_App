package com.example.techhive_app.data.remote.dto.product

data class ProductRemoteDto(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imagen: String,
    val sku: String,
    val categoria: String,
    val stock: Int
)
