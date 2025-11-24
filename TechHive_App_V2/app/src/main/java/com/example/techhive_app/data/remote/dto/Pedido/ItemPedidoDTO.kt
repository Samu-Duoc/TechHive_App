package com.example.techhive_app.data.remote.dto.Pedido

data class ItemPedidoDTO(
    val productoId: String,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: Double
)