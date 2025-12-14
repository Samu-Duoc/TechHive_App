package com.example.techhive_app.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.screen.client.CartScreen
import org.junit.Rule
import org.junit.Test

class CartScreenUiTest {

    @get:Rule
    val rule = createComposeRule()

    private fun product(id: Long, price: Double = 1000.0, stock: Int = 10) = ProductEntity(
        id = id,
        name = "Producto $id",
        description = "Desc",
        price = price,
        imageBase64 = null,
        stock = stock,
        sku = "SKU-$id",
        category = "CAT"
    )

    // Test de UI carrito vacío:
    // - Verifica que se muestre el título y el mensaje de carrito vacío
    @Test
    fun carrito_vacio_muestra_titulo_y_mensaje() {
        Cart.clearCart()

        rule.setContent {
            CartScreen(
                userId = 1L,
                onGoCheckout = {}
            )
        }

        rule.onNodeWithText("Mi carrito").assertIsDisplayed()
        rule.onNodeWithText("Tu carrito está vacío").assertIsDisplayed()
    }

    // Test de UI carrito con productos:
    // - Agrega un item al carrito (estado local)
    // - Verifica que se muestre el producto en pantalla
    @Test
    fun carrito_con_item_muestra_producto() {
        Cart.clearCart()
        Cart.addItem(product(1), 1)

        rule.setContent {
            CartScreen(
                userId = 1L,
                onGoCheckout = {}
            )
        }

        rule.onNodeWithText("Mi carrito").assertIsDisplayed()
        rule.onNodeWithText("Producto 1").assertIsDisplayed()
    }
}
