package com.example.techhive_app.data.remote.dto.Pedido

data class PedidoDTO(
    val pedidoId: String,
    val usuarioId: Long,
    val direccionId: String,
    val total: Double,
    val estado: String,
    val fecha: String
)
