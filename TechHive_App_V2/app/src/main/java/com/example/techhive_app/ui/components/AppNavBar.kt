package com.example.techhive_app.ui.components

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        //INICIO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = { onHome() }
        )

        //CATEGORÍAS
        NavigationBarItem(
            icon = { Icon(Icons.Default.Category, contentDescription = "Categorías") },
            label = { Text("Categorías") },
            selected = false,
            onClick = { onCategories() }
        )

        //CARRITO
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
            label = { Text("Carrito") },
            selected = false,
            onClick = { onCart() }
        )

        //PERFIL / LOGIN
        NavigationBarItem(
            icon = {
                Icon(
                    if (isLoggedIn) Icons.Default.Person else Icons.Default.Login,
                    contentDescription = if (isLoggedIn) "Perfil" else "Iniciar sesión"
                )
            },
            label = { Text(if (isLoggedIn) "Perfil" else "Login") },
            selected = false,
            onClick = {
                if (isLoggedIn) {
                    // Si está logueado, mostrar diálogo para ir al perfil o cerrar sesión
                    showLogoutDialog = true
                } else {
                    // Si no está logueado, ir al Home (logo + botones login/registro)
                    onProfile()
                }
            }
        )
    }

    // Diálogo de cierre de sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Perfil de usuario") },
            text = { Text("¿Deseas ir a tu perfil o cerrar sesión?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onProfile() // Navega al perfil
                }) {
                    Text("Ver perfil")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    scope.launch { onLogout() } // Cierra sesión
                }) {
                    Text("Cerrar sesión")
                }
            }
        )
    }
}
