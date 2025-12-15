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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.techhive_app.data.local.storage.UserPreferences
import com.example.techhive_app.ui.components.AppNavBar
import com.example.techhive_app.ui.screen.admin.AdminHomeScreen
import com.example.techhive_app.ui.screen.admin.AdminOrdersScreen
import com.example.techhive_app.ui.screen.admin.AdminProductGridScreen
import com.example.techhive_app.ui.screen.admin.ProductFormScreen
import com.example.techhive_app.ui.screen.client.CartScreen
import com.example.techhive_app.ui.screen.client.CheckoutScreen
import com.example.techhive_app.ui.screen.client.ContactFormScreen
import com.example.techhive_app.ui.screen.client.MyOrdersScreen
import com.example.techhive_app.ui.screen.client.OrderConfirmationScreen
import com.example.techhive_app.ui.screen.client.ProductDetailScreen
import com.example.techhive_app.ui.screen.client.ProductGridScreen
import com.example.techhive_app.ui.screen.client.TicketScreen
import com.example.techhive_app.ui.screen.common.HomeScreen
import com.example.techhive_app.ui.screen.common.InicioScreen
import com.example.techhive_app.ui.screen.common.LoginScreenVm
import com.example.techhive_app.ui.screen.common.ProfileMenuScreen
import com.example.techhive_app.ui.screen.common.ProfileScreen
import com.example.techhive_app.ui.screen.common.RegisterScreenVm
import com.example.techhive_app.ui.screen.common.SplashDecisionScreen
import com.example.techhive_app.ui.screen.common.SplashScreen
import com.example.techhive_app.ui.screen.common.UnauthorizedScreen
import com.example.techhive_app.ui.screen.common.OrderDetailsScreen
import com.example.techhive_app.ui.screen.common.RecoverPasswordScreen
import com.example.techhive_app.ui.screen.common.EditProfileScreen
import com.example.techhive_app.ui.viewmodel.common.AuthViewModel
import com.example.techhive_app.ui.viewmodel.common.ProductViewModel
import com.example.techhive_app.ui.viewmodel.common.OrderViewerMode
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
    val userId by userPrefs.getUserId.collectAsStateWithLifecycle(initialValue = null)

    val role by userPrefs.role.collectAsStateWithLifecycle(initialValue = null)
    val isAdminUser = role?.equals("ADMIN", ignoreCase = true) == true

    // --- Navegaciones comunes ---
    val goHome: () -> Unit = {
        navController.navigate(Route.Home.path) {
            popUpTo(Route.Home.path) { inclusive = true }
            launchSingleTop = true
        }
    }

    // ✅ LOGIN LIMPIA BACKSTACK (para que no vuelva a pantallas privadas)
    val goLogin: () -> Unit = {
        navController.navigate(Route.Login.path) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val goRegister: () -> Unit = { navController.navigate(Route.Register.path) }

    val goInicio: () -> Unit = {
        navController.navigate(Route.Inicio.path) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }

    val goProducts: () -> Unit = { navController.navigate(Route.ProductList.path) }
    val goToCart: () -> Unit = { navController.navigate(Route.Cart.path) }

    val goToProfile: () -> Unit = {
        if (isLoggedIn && !userEmail.isNullOrBlank()) {
            navController.navigate(Route.ProfileMenu.path)
        } else {
            goLogin()
        }
    }

    // ✅ LOGOUT LIMPIA Y VUELVE A LOGIN
    val onLoggedOut: () -> Unit = {
        CoroutineScope(Dispatchers.IO).launch { userPrefs.clear() }
        goLogin()
    }

    // --- Rutas donde NO va barra de cliente ---
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
        Route.AdminEditProduct.path,
        Route.AdminMessages.path,
        Route.ChangePassword.path,
        Route.EditProfile.path
    )

    // Si es admin, NUNCA mostramos la barra del cliente
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
                    onLogout = { onLoggedOut() }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Route.Splash.path,
            modifier = Modifier.padding(innerPadding)
        ) {

            // ---------- SPLASH ----------
            // ✅ Decide: si hay sesión -> Inicio/Admin, si NO -> Login
            composable(Route.Splash.path) {
                SplashScreen(
                    onTimeout = {
                        val hasSession = isLoggedIn &&
                                !userEmail.isNullOrBlank() &&
                                (userId ?: 0L) > 0L

                        val destination = if (hasSession) {
                            if (isAdminUser) Route.AdminHome.path else Route.Inicio.path
                        } else {
                            Route.Login.path
                        }

                        navController.navigate(destination) {
                            popUpTo(Route.Splash.path) { inclusive = true }
                            launchSingleTop = true
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
                        navController.navigate(Route.SplashDecision.createRoute(email)) {
                            popUpTo(Route.Login.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onGoRegister = goRegister,
                    onForgotPassword = { navController.navigate(Route.ChangePassword.path) }
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
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else if (isAdminUser) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Route.AdminHome.path) {
                            popUpTo(Route.Inicio.path) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                } else {
                    InicioScreen(
                        productViewModel = productViewModel,
                        onCategoryClick = { categoryName ->
                            navController.navigate(Route.ProductListByCategory.createRoute(categoryName))
                        },
                        onViewAllProducts = { goProducts() },
                        onProductClick = { id: Long ->
                            navController.navigate(Route.ProductDetail.createRoute(id))
                        },
                        onContactClick = { navController.navigate(Route.Contact.path) }
                    )
                }
            }

            // ---------- LISTA DE PRODUCTOS ----------
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
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    CartScreen(
                        userId = userId ?: 0L,
                        onGoCheckout = { navController.navigate(Route.Checkout.path) }
                    )
                }
            }

            // ---------- CHECKOUT ----------
            composable(Route.Checkout.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    CheckoutScreen(
                        userId = userId ?: 0L,
                        onBack = { navController.popBackStack() },
                        onPaidNavigateTicket = { navController.navigate(Route.Ticket.path) }
                    )
                }
            }

            // ---------- TICKET ----------
            composable(Route.Ticket.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    TicketScreen(
                        onGoHome = {
                            navController.navigate(Route.Inicio.path) {
                                popUpTo(Route.Inicio.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoHistory = { navController.navigate(Route.OrderHistory.path) }
                    )
                }
            }

            // ---------- COMPROBANTE (ORDEN) ----------
            composable(
                route = Route.OrderConfirmation.path,
                arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
            ) { backStackEntry ->
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
                    OrderConfirmationScreen(
                        pedidoId = pedidoId,
                        onGoHome = {
                            navController.navigate(Route.Inicio.path) {
                                popUpTo(Route.Inicio.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onGoHistory = { navController.navigate(Route.OrderHistory.path) }
                    )
                }
            }

            // ---------- HISTORIAL CLIENTE (PROTEGIDO) ----------
            composable(Route.OrderHistory.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    if (userId == null || userId == 0L) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Debes iniciar sesión para ver tus órdenes.",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Vuelve a iniciar sesión e inténtalo otra vez.")
                        }
                    } else {
                        MyOrdersScreen(
                            usuarioId = userId!!,
                            onOpenOrderDetails = { pedidoId ->
                                navController.navigate(
                                    Route.OrderDetails.createRoute(pedidoId, "client")
                                )
                            }
                        )
                    }
                }
            }

            // ---------- ORDER DETAILS (CLIENT / ADMIN) ----------
            composable(
                route = Route.OrderDetails.path,
                arguments = listOf(
                    navArgument("pedidoId") { type = NavType.StringType },
                    navArgument("mode") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
                    val modeArg = backStackEntry.arguments?.getString("mode") ?: "client"

                    OrderDetailsScreen(
                        pedidoId = pedidoId,
                        mode = if (modeArg.equals("admin", ignoreCase = true))
                            OrderViewerMode.ADMIN
                        else
                            OrderViewerMode.CLIENT,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // ---------- MENÚ PERFIL (PROTEGIDO) ----------
            composable(Route.ProfileMenu.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    ProfileMenuScreen(
                        onProfile = { navController.navigate(Route.Profile.path) },
                        onHistory = { navController.navigate(Route.OrderHistory.path) },
                        onLogout = { onLoggedOut() }
                    )
                }
            }

            // ---------- PERFIL (PROTEGIDO) ----------
            composable(Route.Profile.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    ProfileScreen(
                        authViewModel = authViewModel,
                        onLoggedOut = { onLoggedOut() },
                        onGoChangePassword = { navController.navigate(Route.ChangePassword.path) },
                        onGoEditProfile = { navController.navigate(Route.EditProfile.path) }
                    )
                }
            }

            // ---------- EDITAR PERFIL (PROTEGIDO) ----------
            composable(Route.EditProfile.path) {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) { goLogin() }
                } else {
                    EditProfileScreen(
                        authViewModel = authViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // ---------- RECUPERAR CONTRASEÑA ----------
            composable(Route.ChangePassword.path) {
                // Esta pantalla puede ser pública si tú quieres.
                // Si quieres que sea SOLO con login, cambia a:
                // if (!isLoggedIn) LaunchedEffect(Unit) { goLogin() } else { ... }
                RecoverPasswordScreen(
                    authViewModel = authViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // ---------- CONTACTO ----------
            composable(Route.Contact.path) {
                ContactFormScreen(onBack = { navController.popBackStack() })
            }

            // ================= ADMIN =================

            // PANEL ADMIN
            composable(Route.AdminHome.path) {
                val roleNow by userPrefs.role.collectAsStateWithLifecycle(initialValue = null)
                val isAdmin = roleNow?.equals("ADMIN", ignoreCase = true) == true

                if (!isAdmin) {
                    UnauthorizedScreen(
                        onGoHome = {
                            navController.navigate(Route.Inicio.path) {
                                popUpTo(Route.AdminHome.path) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                } else {
                    AdminHomeScreen(
                        onNavigateToProducts = { navController.navigate(Route.AdminProducts.path) },
                        onNavigateToMessages = { navController.navigate(Route.AdminMessages.path) },
                        onNavigateToOrders = { navController.navigate(Route.AdminOrders.path) },
                        onNavigateToProfile = { navController.navigate(Route.Profile.path) },
                        onAddProduct = { navController.navigate(Route.AdminAddProduct.path) }
                    )
                }
            }

            // GRID CRUD ADMIN
            composable(Route.AdminProducts.path) {
                AdminProductGridScreen(
                    productViewModel = productViewModel,
                    onEditProduct = { id ->
                        navController.navigate(Route.AdminEditProduct.createRoute(id))
                    },
                    onAddProduct = { navController.navigate(Route.AdminAddProduct.path) }
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
                AdminOrdersScreen(
                    onOpenOrderDetails = { pedidoId ->
                        navController.navigate(
                            Route.OrderDetails.createRoute(pedidoId, "admin")
                        )
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

            // MENSAJES ADMIN (CONTACTO)
            composable(Route.AdminMessages.path) {
                val vm = remember { com.example.techhive_app.ui.viewmodel.admin.AdminContactViewModel() }
                com.example.techhive_app.ui.screen.admin.AdminMessagesScreen(
                    viewModel = vm,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
