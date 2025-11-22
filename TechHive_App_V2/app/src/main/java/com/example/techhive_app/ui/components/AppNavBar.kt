package com.example.techhive_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AppNavBar(
    isLoggedIn: Boolean,
    onHome: () -> Unit,
    onCategories: () -> Unit,
    onCart: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val scope = rememberCoroutineScope()

    // Solo para marcar cuál tab está “seleccionada” visualmente
    var selectedIndex by remember { mutableIntStateOf(0) }

    NavigationBar {
        // HOME
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = selectedIndex == 0,
            onClick = {
                selectedIndex = 0
                onHome()
            }
        )

        // CATEGORÍAS / PRODUCTOS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Category, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = selectedIndex == 1,
            onClick = {
                selectedIndex = 1
                onCategories()
            }
        )

        // CARRITO
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
            label = { Text("Carrito") },
            selected = selectedIndex == 2,
            onClick = {
                selectedIndex = 2
                onCart()
            }
        )

        // PERFIL / LOGIN
        NavigationBarItem(
            icon = {
                Icon(
                    if (isLoggedIn) Icons.Default.Person else Icons.Default.Login,
                    contentDescription = if (isLoggedIn) "Cuenta" else "Iniciar sesión"
                )
            },
            label = { Text(if (isLoggedIn) "Cuenta" else "Iniciar Sesión") },
            selected = selectedIndex == 3,
            onClick = {
                selectedIndex = 3
                if (isLoggedIn) {
                    // Si ya está logueado → dejamos que el NavGraph decida adónde ir
                    onProfile()
                } else {
                    // Si NO está logueado → también lo decide el NavGraph (login / home)
                    onProfile()
                }
            }
        )
    }
}
