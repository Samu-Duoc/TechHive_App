package com.example.techhive_app.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import com.example.techhive_app.ui.screen.common.SplashDecisionScreen
import com.example.techhive_app.ui.screen.admin.AdminHomeScreen
import com.example.techhive_app.ui.screen.admin.AdminProductGridScreen
import com.example.techhive_app.ui.screen.admin.ProductFormScreen
import com.example.techhive_app.ui.screen.client.ContactFormScreen
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
    val userEmail by userPrefs.userEmail.collectAsStateWithLifecycle(initialValue = null)

    val isAdminUser = userEmail == "admin@techhive.cl"

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

    val onLoggedOut: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch { userPrefs.clear() }
        goHome()
    }

    // --- Rutas donde NO va barra de cliente (splash, auth, admin, etc.) ---
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val hideClientBarRoutes = setOf(
        Route.Splash.path,
        Route.Home.path,
        Route.Login.path,
        Route.Register.path,
        Route.SplashDecision.path,
        Route.AdminHome.path,
        Route.AdminProducts.path,
        Route.AdminAddProduct.path,
        Route.AdminEditProduct.path
    )

    // si es admin, NUNCA mostramos la barra del cliente
    val showClientBar = !isAdminUser && currentRoute !in hideClientBarRoutes

    Scaffold(
        bottomBar = {
            if (showClientBar) {
                AppNavBar(
                    isLoggedIn = isLoggedIn,
                    onHome = { goInicio() },
                    onCategories = { goProducts() },
                    onCart = { goToCart() },
                    onProfile = { goToProfile() },
                    onLogout = {
                        CoroutineScope(Dispatchers.IO).launch { userPrefs.clear() }
                        goHome()
                    }
                )
            }
        }
    ) { innerPadding ->
        // ============ NAVHOST ÚNICO ============
        NavHost(
            navController = navController,
            startDestination = Route.Splash.path,   // SIEMPRE arranca en Splash
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------- SPLASH ----------
            composable(Route.Splash.path) {
                SplashScreen(
                    onTimeout = {
                        if (isLoggedIn) {
                            val destination =
                                if (isAdminUser) Route.AdminHome.path
                                else Route.Inicio.path

                            navController.navigate(destination) {
                                popUpTo(Route.Splash.path) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Route.Home.path) {
                                popUpTo(Route.Splash.path) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ---------- HOME PÚBLICO ----------
            composable(Route.Home.path) {
                HomeScreen(
                    onGoLogin = goLogin,
                    onGoRegister = goRegister
                )
            }

            // ---------- LOGIN ----------
            composable(Route.Login.path) {
                LoginScreenVm(
                    vm = authViewModel,
                    onLoginOkNavigateHome = {
                        val email = authViewModel.login.value.email
                        navController.navigate(
                            Route.SplashDecision.createRoute(email)
                        ) {
                            popUpTo(Route.Home.path) { inclusive = true }
                        }
                    },
                    onGoRegister = goRegister
                )
            }

            // ---------- REGISTER ----------
            composable(Route.Register.path) {
                RegisterScreenVm(
                    vm = authViewModel,
                    onRegisteredNavigateLogin = goLogin,
                    onGoLogin = goLogin
                )
            }

            // ---------- SPLASH DECISION ----------
            composable(
                route = Route.SplashDecision.path,
                arguments = listOf(navArgument("email") { type = NavType.StringType })
            ) { backStackEntry ->
                val email = backStackEntry.arguments?.getString("email") ?: ""
                SplashDecisionScreen(
                    navController = navController,
                    email = email
                )
            }

            // ---------- INICIO CLIENTE ----------
            composable(Route.Inicio.path) {
                if (isAdminUser) {
                    // Si por alguna razón el admin llega aquí, lo mandamos al panel admin
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.AdminHome.path) {
                            popUpTo(Route.AdminHome.path) { inclusive = true }
                        }
                    }
                } else {
                    InicioScreen(
                        productViewModel = productViewModel,
                        onCategoryClick = { categoryName ->
                            navController.navigate(
                                Route.ProductListByCategory.createRoute(categoryName)
                            )
                        },
                        onViewAllProducts = { goProducts() },
                        onProductClick = { id: Long ->
                            navController.navigate(Route.ProductDetail.createRoute(id))
                        },
                        onContactClick = {
                            navController.navigate(Route.Contact.path)
                        }
                    )
                }
            }

            // ---------- LISTA DE PRODUCTOS (GRID GENERAL) ----------
            composable(Route.ProductList.path) {
                ProductGridScreen(
                    productViewModel = productViewModel,
                    onProductClick = { productId: Long ->
                        navController.navigate(Route.ProductDetail.createRoute(productId))
                    }
                )
            }

            // ---------- LISTA DE PRODUCTOS POR CATEGORÍA ----------
            composable(
                route = Route.ProductListByCategory.path,
                arguments = listOf(navArgument("category") { type = NavType.StringType })
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category")
                ProductGridScreen(
                    productViewModel = productViewModel,
                    onProductClick = { productId: Long ->
                        navController.navigate(Route.ProductDetail.createRoute(productId))
                    },
                    initialCategory = category
                )
            }

            // ---------- DETALLE PRODUCTO ----------
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

            // ---------- CARRITO ----------
            composable(Route.Cart.path) {
                CartScreen(
                    onCheckout = { orderId ->
                        if (orderId != -1L) {
                            navController.navigate(Route.OrderConfirmation.createRoute(orderId))
                        }
                    }
                )
            }

            // ---------- COMPROBANTE (ORDEN) ----------
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

            // ---------- HISTORIAL CLIENTE ----------
            composable(Route.OrderHistory.path) {
                OrderHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOrderSelected = { id ->
                        navController.navigate(Route.OrderConfirmation.createRoute(id))
                    }
                )
            }

            // ---------- MENÚ PERFIL ----------
            composable(Route.ProfileMenu.path) {
                ProfileMenuScreen(
                    onEditProfile = { navController.navigate(Route.Profile.path) },
                    onAddress = { navController.navigate(Route.Address.path) },
                    onHistory = { navController.navigate(Route.OrderHistory.path) },
                    onLogout = { onLoggedOut() }
                )
            }

            // ---------- DIRECCIÓN ----------
            composable(Route.Address.path) {
                AddressScreen(onBack = { navController.popBackStack() })
            }

            // ---------- CONTACTO ----------
            composable(Route.Contact.path) {
                ContactFormScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            // ---------- PERFIL ----------
            composable(Route.Profile.path) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLoggedOut = { onLoggedOut() }
                )
            }

            // =============== RUTAS ADMIN =================

            // PANEL ADMIN
            composable(Route.AdminHome.path) {
                AdminHomeScreen(
                    onNavigateToProducts = {
                        navController.navigate(Route.AdminProducts.path)
                    },
                    onNavigateToUsers = {
                        navController.navigate(Route.AdminUsers.path)
                    },
                    onNavigateToOrders = {
                        navController.navigate(Route.AdminOrders.path)
                    },
                    onNavigateToProfile = {
                        navController.navigate(Route.Profile.path)
                    },
                    onAddProduct = {
                        navController.navigate(Route.AdminAddProduct.path)
                    }
                )
            }

            // GRID CRUD ADMIN
            composable(Route.AdminProducts.path) {
                AdminProductGridScreen(
                    productViewModel = productViewModel,
                    onEditProduct = { id ->
                        navController.navigate(Route.AdminEditProduct.createRoute(id))
                    }
                )
            }

            // NUEVO PRODUCTO (ADMIN)
            composable(Route.AdminAddProduct.path) {
                ProductFormScreen(
                    productViewModel = productViewModel,
                    productId = null,
                    onFinished = { navController.popBackStack() }
                )
            }

            // EDITAR PRODUCTO (ADMIN)
            composable(
                route = Route.AdminEditProduct.path,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId")
                ProductFormScreen(
                    productViewModel = productViewModel,
                    productId = productId,
                    onFinished = { navController.popBackStack() }
                )
            }

            // LISTADO DE PEDIDOS PARA ADMIN
            composable(Route.AdminOrders.path) {
                OrderHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOrderSelected = { id ->
                        navController.navigate(Route.OrderConfirmation.createRoute(id))
                    }
                )
            }

            // USUARIOS ADMIN (placeholder)
            composable(Route.AdminUsers.path) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Usuarios (próximamente)",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Esta sección mostrará los clientes registrados desde el microservicio Auth.")
                }
            }
        }
    }
}
