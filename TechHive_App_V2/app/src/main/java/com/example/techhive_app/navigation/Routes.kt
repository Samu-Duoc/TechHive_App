    package com.example.techhive_app.navigation

    sealed class Route(val path: String) {

        data object Splash   : Route("splash") //Pantalla de carga
        data object Inicio   : Route("inicio") // Inicio de app, con categorías y productos destacados
        data object Home     : Route("home")  //Pantalla pública sin sesión
        data object Login    : Route("login")  //Pantalla de Iniciar Sesión
        data object Register : Route("register")  //Pantalla de Registro
        data object ProductList : Route("products")  //Productos
        data object Cart : Route("cart")  // Carrito
        data object Profile : Route("profile") // Perfil

        data object ProfileMenu : Route("profile_menu") // TopBar de Cuenta

        data object Address : Route("address") // navegación a dirección



        //productos en detalle (visulización de un producto)
        data object ProductDetail : Route("products/{productId}") {
            fun createRoute(productId: Long) = "products/$productId"
        }

        //Comporbante
        data object OrderConfirmation : Route("order_confirmation/{orderId}") {
            fun createRoute(orderId: Long) = "order_confirmation/$orderId"
        }

        //Historial de compras
        data object OrderHistory : Route("order_history")



    }


