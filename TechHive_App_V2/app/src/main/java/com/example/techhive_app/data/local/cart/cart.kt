package com.example.techhive_app.data.local.cart

import com.example.techhive_app.data.local.product.ProductEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Representa un ítem en el carrito de compras con un producto y su cantidad
data class CartItem(val product: ProductEntity, val quantity: Int)

// Objeto singleton que gestiona el estado del carrito de compras.
object Cart {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items

    /**
     * Añade un producto al carrito.
     * @param quantity cuántas unidades quieres sumar (por defecto 1)
     */
    fun addItem(product: ProductEntity, quantity: Int = 1) {
        if (quantity <= 0) return

        val currentItems = _items.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == product.id }

        if (index >= 0) {
            val existing = currentItems[index]
            val newQty = existing.quantity + quantity
            currentItems[index] = existing.copy(quantity = newQty)
        } else {
            currentItems.add(CartItem(product = product, quantity = quantity))
        }

        _items.value = currentItems
    }

    /**
     * Actualiza la cantidad de un producto.
     * Si newQuantity <= 0, se elimina del carrito.
     */
    fun updateQuantity(productId: Long, newQuantity: Int) {
        val currentItems = _items.value.toMutableList()
        val index = currentItems.indexOfFirst { it.product.id == productId }

        if (index >= 0) {
            if (newQuantity <= 0) {
                currentItems.removeAt(index)
            } else {
                val existing = currentItems[index]
                currentItems[index] = existing.copy(quantity = newQuantity)
            }
            _items.value = currentItems
        }
    }

    //suma +1
    fun increaseQuantity(productId: Long) {
        val current = _items.value
        val item = current.firstOrNull { it.product.id == productId } ?: return
        updateQuantity(productId, item.quantity + 1)
    }

    //resta -1 (mínimo 1)
    fun decreaseQuantity(productId: Long) {
        val current = _items.value
        val item = current.firstOrNull { it.product.id == productId } ?: return
        val newQty = (item.quantity - 1).coerceAtLeast(1)
        updateQuantity(productId, newQty)
    }

    // Elimina un producto del carrito
    fun removeItem(productId: Long) {
        val currentItems = _items.value.toMutableList()
        currentItems.removeAll { it.product.id == productId }
        _items.value = currentItems
    }

    // Vacía todos los productos del carrito
    fun clearCart() {
        _items.value = emptyList()
    }
}
