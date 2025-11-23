// common/SplashDecisionScreen.kt
package com.example.techhive_app.ui.screen.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.techhive_app.navigation.Route


@Composable
fun SplashDecisionScreen(
    navController: NavController,
    email: String
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Cargando experiencia para $email...")
        }
    }

    LaunchedEffect(email) {
        val destino = if (email == "admin@techhive.cl") {
            Route.AdminHome.path
        } else {
            Route.Inicio.path
        }
        navController.navigate(destino) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
    }
}