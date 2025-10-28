package com.example.techhive_app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
import com.example.techhive_app.ui.screen.*
import com.example.techhive_app.ui.viewmodel.AuthViewModel
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import kotlinx.coroutines.*

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
    val goHome: () -> Unit = { navController.navigate(Route.Home.path) { popUpTo(Route.Home.path) { inclusive = true } } }
    val goLogin: () -> Unit = { navController.navigate(Route.Login.path) }
    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }
    val goInicio: () -> Unit = { navController.navigate(Route.Inicio.path) { popUpTo(0) { inclusive = true } } }
    val goProducts: () -> Unit = { navController.navigate(Route.ProductList.path) }
    val goToCart: () -> Unit = { navController.navigate(Route.Cart.path) }
    val goToProfile: () -> Unit = { if (isLoggedIn) navController.navigate(Route.Profile.path) else goLogin() }
    val onLoggedOut: () -> Unit = { goHome() }

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route


            val shouldShowBottomBar = currentRoute !in listOf(
                Route.Home.path,
                Route.Login.path,
                Route.Register.path
            )

            if (shouldShowBottomBar) {
                AppNavBar(
                    isLoggedIn = isLoggedIn,
                    onHome = { goInicio() },
                    onCategories = { goProducts() },
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
            startDestination = if (isLoggedIn) Route.Inicio.path else Route.Home.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            // HOME - pantalla inicial pública
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



            // INICIO (categorías)
            composable(Route.Inicio.path) {
                InicioScreen(
                    onCategoryClick = { goProducts() }
                )
            }

            // LISTA DE PRODUCTOS
            composable(Route.ProductList.path) {
                ProductListScreen(
                    productViewModel = productViewModel,
                    onProductClick = { productId ->
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
                    productViewModel = productViewModel
                )
            }

            // CARRITO
            composable(Route.Cart.path) {
                CartScreen()
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
