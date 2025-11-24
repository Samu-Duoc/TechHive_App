package com.example.techhive_app.data.remote.dto.Pedido

import com.example.techhive_app.data.remote.dto.Pedido.ItemPedidoDTO

data class CrearPedidoPagoDTO(
    val usuarioId: String,
    val direccionId: String,
    val metodoPago: String,
    val total: Double,
    val items: List<ItemPedidoDTO>
)