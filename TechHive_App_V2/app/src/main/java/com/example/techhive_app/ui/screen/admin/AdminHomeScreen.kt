package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// Modelo de opción del admin
data class AdminOption(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(vertical = 8.dp)
    ) {
        // Título grande de bienvenida
        Text(
            text = "Panel de Administrador",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(16.dp)
        )

        // Banner promocional igual que el cliente (opcional, lo puedes quitar si quieres)
        AdminPromotionalBanner()

        // Tarjetas grandes de opciones (estilo moderno)
        AdminQuickActions(
            onNavigateToProducts = onNavigateToProducts,
            onNavigateToUsers = onNavigateToUsers,
            onNavigateToOrders = onNavigateToOrders,
            onNavigateToReports = onNavigateToReports
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Estadísticas rápidas (puedes conectar después con tu ViewModel)
        QuickStatsSection()
    }
}

@Composable
fun AdminPromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AdminPanelSettings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(
                    "Bienvenido, Administrador",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Gestiona tu tienda desde aquí",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun AdminQuickActions(
    onNavigateToProducts: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToReports: () -> Unit
) {
    val options = listOf(
        AdminOption("Productos", Icons.Default.Inventory2) { onNavigateToProducts() },
        AdminOption("Usuarios", Icons.Default.People) { onNavigateToUsers() },
        AdminOption("Pedidos", Icons.Default.ReceiptLong) { onNavigateToOrders() },
        AdminOption("Reportes", Icons.Default.BarChart) { onNavigateToReports() }
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Acciones rápidas",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(options) { option ->
                AdminActionCard(option = option)
            }
        }
    }
}

@Composable
fun AdminActionCard(option: AdminOption) {
    Card(
        onClick = option.onClick,
        modifier = Modifier
            .width(160.dp)
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = option.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun QuickStatsSection() {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Resumen rápido",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "Ventas hoy", value = "$12,450", icon = Icons.Default.AttachMoney)
            StatCard(title = "Pedidos", value = "48", icon = Icons.Default.ShoppingCart)
            StatCard(title = "Productos", value = "156", icon = Icons.Default.Inventory)
        }
    }
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}