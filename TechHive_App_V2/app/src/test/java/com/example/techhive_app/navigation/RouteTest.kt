package com.example.techhive_app.navigation

import org.junit.Assert.*
import org.junit.Test

class RouteTest {

    @Test
    fun productDetail_createRoute_genera_ruta_correcta() {
        val route = Route.ProductDetail.createRoute(15L)
        assertEquals("products/15", route)
    }

    @Test
    fun orderConfirmation_createRoute_genera_ruta_correcta() {
        val route = Route.OrderConfirmation.createRoute("ABC123")
        assertEquals("order_confirmation/ABC123", route)
    }

    @Test
    fun orderDetails_createRoute_genera_ruta_correcta() {
        val route = Route.OrderDetails.createRoute("P001", "client")
        assertEquals("order_details/P001/client", route)
    }

    @Test
    fun adminEditProduct_createRoute_genera_ruta_correcta() {
        val route = Route.AdminEditProduct.createRoute(99L)
        assertEquals("admin_edit_product/99", route)
    }

    @Test
    fun splashDecision_createRoute_genera_ruta_correcta() {
        val route = Route.SplashDecision.createRoute("a@b.com")
        assertEquals("splash_decision/a@b.com", route)
    }
}
