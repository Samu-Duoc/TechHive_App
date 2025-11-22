package com.example.techhive_app.data.local.order

import com.example.techhive_app.data.local.cart.CartItem

data class Order(
    val id: Long,
    val date: String,
    val total: Double,
    val items: List<CartItem>,
    val status: String = "Pagado"
)