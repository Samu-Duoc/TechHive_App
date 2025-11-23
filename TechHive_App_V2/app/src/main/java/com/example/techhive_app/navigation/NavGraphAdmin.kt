package com.example.techhive_app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.techhive_app.ui.components.AdminBottomBar
import com.example.techhive_app.ui.screen.admin.AdminProductGridScreen
import com.example.techhive_app.ui.screen.admin.ProductFormScreen
import com.example.techhive_app.ui.screen.client.AddressScreen
import com.example.techhive_app.ui.screen.client.OrderHistoryScreen
import com.example.techhive_app.ui.screen.common.ProfileMenuScreen
import com.example.techhive_app.ui.screen.common.ProfileScreen
import com.example.techhive_app.ui.viewmodel.AuthViewModel
import com.example.techhive_app.ui.viewmodel.ProductViewModel

@Composable
fun NavGraphAdmin(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    productViewModel: ProductViewModel,
    onLoggedOut: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            AdminBottomBar(
                currentRoute = currentRoute,
                onHome = {
                    navController.navigate(Route.AdminProducts.path) {
                        popUpTo(Route.AdminProducts.path) { inclusive = true }
                    }
                },
                onProducts = {
                    navController.navigate(Route.AdminProducts.path)
                },
                onAddProduct = {
                    navController.navigate(Route.AdminAddProduct.path)
                },
                onProfile = {
                    navController.navigate(Route.ProfileMenu.path)
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.AdminProducts.path,
            modifier = Modifier.padding(innerPadding)
        ) {
            // GRID CRUD
            composable(Route.AdminProducts.path) {
                AdminProductGridScreen(
                    productViewModel = productViewModel,
                    onEditProduct = { id ->
                        navController.navigate(Route.AdminEditProduct.createRoute(id))
                    }
                )
            }

            // NUEVO PRODUCTO
            composable(Route.AdminAddProduct.path) {
                ProductFormScreen(
                    productViewModel = productViewModel,
                    productId = null,
                    onFinished = { navController.popBackStack() }
                )
            }

            // EDITAR PRODUCTO
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

            // MENÚ CUENTA (para admin)
            composable(Route.ProfileMenu.path) {
                ProfileMenuScreen(
                    onEditProfile = { navController.navigate(Route.Profile.path) },
                    onAddress = { navController.navigate(Route.Address.path) },
                    onHistory = { navController.navigate(Route.OrderHistory.path) },
                    onLogout = onLoggedOut,
                    historyLabel = "Órdenes"   // 👈 aquí cambia el texto
                )
            }

            // PERFIL
            composable(Route.Profile.path) {
                ProfileScreen(
                    authViewModel = authViewModel,
                    onLoggedOut = onLoggedOut
                )
            }

            // DIRECCIÓN
            composable(Route.Address.path) {
                AddressScreen(onBack = { navController.popBackStack() })
            }

            // ÓRDENES (reusamos el historial que ya tienes)
            composable(Route.OrderHistory.path) {
                OrderHistoryScreen(
                    onBack = { navController.popBackStack() },
                    onOrderSelected = { /* podrías abrir detalle si quieres */ }
                )
            }
        }
    }
}


