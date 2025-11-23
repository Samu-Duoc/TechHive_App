package com.example.techhive_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun AdminBottomBar(
    currentRoute: String?,
    onHome: () -> Unit,
    onProducts: () -> Unit,
    onAddProduct: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = currentRoute == "admin_home",
            onClick = onHome
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = currentRoute == "admin_products",
            onClick = onProducts
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Nuevo") },
            label = { Text("Nuevo") },
            selected = currentRoute == "admin_add_product",
            onClick = onAddProduct
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Cuenta") },
            label = { Text("Cuenta") },
            selected = currentRoute == "profile_menu",
            onClick = onProfile
        )
    }
}
