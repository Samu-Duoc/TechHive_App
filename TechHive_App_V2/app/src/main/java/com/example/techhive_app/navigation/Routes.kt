    package com.example.techhive_app.navigation

    sealed class Route(val path: String) {

        //Rutas generales
        data object Splash   : Route("splash") //Pantalla de carga
        data object Home     : Route("home")  //Pantalla pública sin sesión
        data object Login    : Route("login")  //Pantalla de Iniciar Sesión
        data object Register : Route("register")  //Pantalla de Registro


        //Rutas del Cliente en la app
        data object Inicio   : Route("inicio") // Inicio de app, con categorías y productos destacados
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


        //Rutas del Admin en la app
        data object AdminProducts : Route("admin_products")
        data object AdminAddProduct : Route("admin_add_product")
        data object AdminEditProduct : Route("admin_edit_product/{productId}") {
            fun createRoute(productId: Long) = "admin_edit_product/$productId"
        }

        data object AdminOrders : Route("admin_orders")

        data object AdminUsers  : Route("admin_users")

        //Nuevas rutas para mostrar el admin
        data object SplashDecision : Route("splash_decision/{email}") {
            fun createRoute(email: String) = "splash_decision/$email"
        }
        object AdminHome : Route("admin_home")  // entrada al NavGraph del admin


        // LISTA GENERAL
        data object ProductList : Route("products")

        // LISTA FILTRADA POR CATEGORÍA
        data object ProductListByCategory : Route("products_by_category/{category}") {
            fun createRoute(category: String) = "products_by_category/$category"
        }



    }


