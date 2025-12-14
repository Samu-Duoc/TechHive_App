package com.example.techhive_app.data.remote.dto.Pedido
data class PedidoDetalleDTO(
    val pedidoId: String,
    val usuarioId: Long,
    val direccionId: String,
    val total: Double,
    val estado: String,
    val fecha: String,
    val metodoPago: String,
    val items: List<ItemDetalleDTO>
)