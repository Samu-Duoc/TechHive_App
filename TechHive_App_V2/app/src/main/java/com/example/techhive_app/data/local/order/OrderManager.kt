package com.example.techhive_app.data.local.order

import com.example.techhive_app.data.local.cart.Cart
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object OrderManager {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    fun createOrderFromCart(): Long {
        val items = Cart.items.value
        if (items.isEmpty()) return -1L

        val newId = (_orders.value.maxOfOrNull { it.id } ?: 0L) + 1L
        val total = items.sumOf { it.product.price * it.quantity }

        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val dateString = formatter.format(Date())

        val order = Order(
            id = newId,
            date = dateString,
            total = total,
            items = items.toList()
        )

        _orders.value = _orders.value + order

        Cart.clearCart()

        return newId
    }

    fun getOrderById(id: Long): Order? =
        _orders.value.find { it.id == id }
}
