package com.example.techhive_app.data.remote.dto.Pedido

data class ComprobantePagoDTO(
    val mensaje: String,
    val pedidoId: String,
    val fecha: String,
    val total: Double,
    val metodoPago: String
)