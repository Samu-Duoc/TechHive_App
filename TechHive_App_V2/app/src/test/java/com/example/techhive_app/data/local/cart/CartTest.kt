package com.example.techhive_app.data.local.cart

import com.example.techhive_app.data.local.product.ProductEntity
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CartTest {

    // Test de agregar producto al carrito: 1.Verifica que el estado local del carrito se actualice correctamente
    //                                      2. No se usa API ni base de datos
    //                                      3.Se espera que el producto quede almacenado con la cantidad correcta

    private fun product(id: Long, price: Double = 1000.0): ProductEntity {
        return ProductEntity(
            id = id,
            name = "Producto $id",
            description = "Desc",
            price = price,
            imageBase64 = null,
            stock = 10,
            sku = "SKU-$id",
            category = "CAT"
        )
    }

    @Before
    fun setup() {
        Cart.clearCart()
    }

    @Test
    fun addItem_agrega_nuevo_item_al_carrito() {
        Cart.addItem(product(1), quantity = 1)

        val items = Cart.items.value
        assertEquals(1, items.size)
        assertEquals(1L, items[0].product.id)
        assertEquals(1, items[0].quantity)
    }

    @Test
    fun addItem_mismo_producto_suma_cantidad() {
        val p = product(1)
        Cart.addItem(p, 1)
        Cart.addItem(p, 2)

        val items = Cart.items.value
        assertEquals(1, items.size)
        assertEquals(3, items[0].quantity)
    }

    @Test
    fun addItem_quantity_cero_no_hace_nada() {
        Cart.addItem(product(1), quantity = 0)
        assertTrue(Cart.items.value.isEmpty())
    }

    @Test
    fun updateQuantity_si_newQuantity_es_cero_elimina_item() {
        Cart.addItem(product(1), 2)
        Cart.updateQuantity(productId = 1L, newQuantity = 0)

        assertTrue(Cart.items.value.isEmpty())
    }

    @Test
    fun updateQuantity_actualiza_cantidad() {
        Cart.addItem(product(1), 1)
        Cart.updateQuantity(productId = 1L, newQuantity = 5)

        val item = Cart.items.value.first()
        assertEquals(5, item.quantity)
    }

    @Test
    fun increaseQuantity_suma_1() {
        Cart.addItem(product(1), 1)
        Cart.increaseQuantity(1L)

        val item = Cart.items.value.first()
        assertEquals(2, item.quantity)
    }

    @Test
    fun decreaseQuantity_baja_1_pero_minimo_1() {
        Cart.addItem(product(1), 1)
        Cart.decreaseQuantity(1L)

        val item = Cart.items.value.first()
        assertEquals(1, item.quantity) // no baja de 1
    }

    @Test
    fun removeItem_elimina_producto_por_id() {
        Cart.addItem(product(1), 1)
        Cart.addItem(product(2), 1)

        Cart.removeItem(1L)

        val items = Cart.items.value
        assertEquals(1, items.size)
        assertEquals(2L, items[0].product.id)
    }

    @Test
    fun clearCart_deja_vacio() {
        Cart.addItem(product(1), 1)
        Cart.addItem(product(2), 1)

        Cart.clearCart()

        assertTrue(Cart.items.value.isEmpty())
    }
}
