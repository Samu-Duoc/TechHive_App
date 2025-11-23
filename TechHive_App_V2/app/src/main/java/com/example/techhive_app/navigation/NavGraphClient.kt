package com.example.techhive_app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue    // 👈 AGREGA ESTO
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.ui.components.AppNavBar
import com.example.techhive_app.ui.screen.client.AddressScreen
import com.example.techhive_app.ui.screen.client.CartScreen
import com.example.techhive_app.ui.screen.client.OrderConfirmationScreen
import com.example.techhive_app.ui.screen.client.OrderHistoryScreen
import com.example.techhive_app.ui.screen.client.ProductDetailScreen
import com.example.techhive_app.ui.screen.client.ProductGridScreen
import com.example.techhive_app.ui.screen.common.HomeScreen
import com.example.techhive_app.ui.screen.common.InicioScreen
import com.example.techhive_app.ui.screen.common.LoginScreenVm
import com.example.techhive_app.ui.screen.common.ProfileMenuScreen
import com.example.techhive_app.ui.screen.common.ProfileScreen
import com.example.techhive_app.ui.screen.common.RegisterScreenVm
import com.example.techhive_app.ui.screen.common.SplashScreen
import com.example.techhive_app.ui.viewmodel.AuthViewModel
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel
) {
    // --- Estado de sesión ---
    val context = LocalContext.current
    val userPrefs = remember { UserPreferences(context) }
    val isLoggedIn by userPrefs.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)

    // --- Navegaciones comunes ---
    val goHome: () -> Unit = {
        navController.navigate(Route.Home.path) {
            popUpTo(Route.Home.path) { inclusive = true }
        }
    }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goInicio: () -> Unit = {
        navController.navigate(Route.Inicio.path) {
            popUpTo(0) { inclusive = true }
        }
    }
    val goProducts: () -> Unit = { navController.navigate(Route.ProductList.path) }
    val goToCart: () -> Unit = { navController.navigate(Route.Cart.path) }
    val goToProfile: () -> Unit = {
        if (isLoggedIn) navController.navigate(Route.ProfileMenu.path) else goLogin()
    }

// ...

    val onLoggedOut: () -> Unit = { goHome() }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val shouldShowBottomBar = currentRoute !in listOf(
                Route.Splash.path,   // ocultar bottom bar en Splash
                Route.Home.path,
                Route.Login.path,
                Route.Register.path
            )

            if (shouldShowBottomBar) {
                AppNavBar(
                    isLoggedIn = isLoggedIn,
                    onHome = { goInicio() },
                    onCategories = { goProducts() },   // aquí luego le cambias el texto a "Productos"
                    onCart = { goToCart() },
                    onProfile = { goToProfile() },
                    onLogout = {
                        CoroutineScope(Dispatchers.IO).launch {
                            userPrefs.clear()
                        }
                        goHome()
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.Splash.path,   // SIEMPRE arranca en Splash
            modifier = Modifier.padding(innerPadding)
        ) {
            // SPLASH
            composable(Route.Splash.path) {
                SplashScreen(
                    onTimeout = {
                        if (isLoggedIn) {
                            // Si ya está logueado -> va a Inicio
                            navController.navigate(Route.Inicio.path) {
                                popUpTo(Route.Splash.path) { inclusive = true }
                            }
                        } else {
                            // Si no está logueado -> va a Home público
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Splash.path) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // HOME - pantalla inicial pública (landing sin sesión)
            composable(Route.Home.path) {
                HomeScreen(
                    onGoLogin = goLogin,
                    onGoRegister = goRegister
                )
            }

            // LOGIN
            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = authViewModel,
                    onLoginOkNavigateHome = goInicio,
                    onGoRegister = goRegister
                )
            }

            // REGISTER
            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = authViewModel,
                    onRegisteredNavigateLogin = goLogin,
                    onGoLogin = goLogin
                )
            }

            // INICIO (categorías + destacados)
            composable(Route.Inicio.path) {
                InicioScreen(
                    productViewModel = productViewModel,
                    onCategoryClick = { _ ->
                        // Por ahora al tocar cualquier categoría abrimos lista completa
                        goProducts()
                    },
                    onViewAllProducts = { goProducts() },     // "Ver todos" -> lista de productos (grid)
                    onProductClick = { id: Long ->
                        navController.navigate(Route.ProductDetail.createRoute(id))
                    }
                )
            }

            // LISTA DE PRODUCTOS COMO GRID
            composable(Route.ProductList.path) {
                ProductGridScreen(
                    productViewModel = productViewModel,
                    onProductClick = { productId: Long ->
                        navController.navigate(Route.ProductDetail.createRoute(productId))
                    }
                )
            }

            // DETALLE DEL PRODUCTO
            composable(
                route = Route.ProductDetail.path,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
                ProductDetailScreen(
                    productId = productId,
                    productViewModel = productViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // CARRITO
            composable(Route.Cart.path) {
                CartScreen(
                    onCheckout = { orderId ->
                        if (orderId != -1L) {
                            navController.navigate(Route.OrderConfirmation.createRoute(orderId))
                        }
                    }
                )
            }

            // COMPROBANTE / DETALLE DE ORDEN
            composable(
                route = Route.OrderConfirmation.path,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: -1L
                OrderConfirmationScreen(
                    orderId = orderId,
                    onGoHome = {
                        navController.navigate(Route.Inicio.path) {
                            popUpTo(Route.Inicio.path) { inclusive = true }
                        }
                    },
                    onGoHistory = {
                        navController.navigate(Route.OrderHistory.path)
                    }
                )
            }

            // HISTORIAL DE COMPRAS
            composable(Route.OrderHistory.path) {
                OrderHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOrderSelected = { id ->
                        navController.navigate(Route.OrderConfirmation.createRoute(id))
                    }
                )
            }

            // PANTALLA DE MENU DEL PERFIL
            composable(Route.ProfileMenu.path) {

                ProfileMenuScreen(
                    onEditProfile = { navController.navigate(Route.Profile.path) },
                    onAddress = { navController.navigate("address") },
                    onHistory = { navController.navigate(Route.OrderHistory.path) },
                    onLogout = {
                        onLoggedOut()
                    }
                )
            }

            // DIRECCIÓN DE ENVÍO
            composable("address") {
                AddressScreen(onBack = { navController.popBackStack() })
            }

            // PERFIL
            composable(Route.Profile.path) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}
