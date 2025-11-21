    package com.example.techhive_app.navigation

    sealed class Route(val path: String) {
        data object Inicio   : Route("inicio")
        data object Home     : Route("home")
        data object Login    : Route("login")
        data object Register : Route("register")
        data object ProductList : Route("products")
        data object Cart : Route("cart")
        data object Profile : Route("profile")

        data object ProductDetail : Route("products/{productId}") {
            fun createRoute(productId: Long) = "products/$productId"
        }
    }


