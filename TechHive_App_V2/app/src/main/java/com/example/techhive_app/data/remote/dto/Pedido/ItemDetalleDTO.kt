package com.example.techhive_app.data.remote.dto.Pedido

data class ItemDetalleDTO(
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)