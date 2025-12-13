package com.example.techhive_app.navigation

sealed class Route(val path: String) {

    //Rutas generales
    data object Splash   : Route("splash")
    data object Home     : Route("home")
    data object Login    : Route("login")
    data object Register : Route("register")
    data object ChangePassword : Route("change_password")

    //Rutas Cliente
    data object Inicio   : Route("inicio")
    data object Cart : Route("cart")
    data object Profile : Route("profile")
    data object ProfileMenu : Route("profile_menu")
    data object Address : Route("address")

    data object ProductDetail : Route("products/{productId}") {
        fun createRoute(productId: Long) = "products/$productId"
    }

    //Comprobante
    data object OrderConfirmation : Route("order_confirmation/{pedidoId}") {
        fun createRoute(pedidoId: String) = "order_confirmation/$pedidoId"
    }

    data object OrderHistory : Route("order_history")

    //Admin
    data object AdminProducts : Route("admin_products")
    data object AdminAddProduct : Route("admin_add_product")
    data object AdminEditProduct : Route("admin_edit_product/{productId}") {
        fun createRoute(productId: Long) = "admin_edit_product/$productId"
    }

    data object AdminOrders : Route("admin_orders")
    data object AdminUsers  : Route("admin_users")
    data object AdminMessages : Route("admin_messages")

    data object SplashDecision : Route("splash_decision/{email}") {
        fun createRoute(email: String) = "splash_decision/$email"
    }

    object AdminHome : Route("admin_home")

    // Productos
    data object ProductList : Route("products")
    data object ProductListByCategory : Route("products_by_category/{category}") {
        fun createRoute(category: String) = "products_by_category/$category"
    }

    // Contacto
    data object Contact : Route("contact")

    // Comprobantes
    data object Checkout : Route("checkout")
    data object Ticket : Route("ticket")
}
