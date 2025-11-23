package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Home de Admin con acciones rápidas
@Composable
fun AdminHomeScreen(
    onNavigateToProducts: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onAddProduct: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // --------- CABECERA ADMIN ---------
            AdminHeaderCard()

            Spacer(modifier = Modifier.height(24.dp))

            // --------- ACCIONES RÁPIDAS ---------
            Text(
                text = "Acciones rápidas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Grid 2x2 de acciones rápidas
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        title = "Inventario",
                        description = "Ver y editar productos",
                        icon = Icons.Default.Inventory2,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProducts
                    )
                    QuickActionCard(
                        title = "Pedidos",
                        description = "Órdenes y estados",
                        icon = Icons.Default.ListAlt,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToOrders
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickActionCard(
                        title = "Clientes",
                        description = "Usuarios registrados",
                        icon = Icons.Default.Group,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToUsers
                    )
                    QuickActionCard(
                        title = "Cuenta",
                        description = "Perfil de administrador",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProfile
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --------- AGREGAR PRODUCTO DESTACADO ---------
            Text(
                text = "Gestión de productos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            AddProductCard(onAddProduct = onAddProduct)

            Spacer(modifier = Modifier.height(24.dp))

            // --------- ESTADÍSTICAS RÁPIDAS (placeholder) ---------
            Text(
                text = "Resumen rápido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val stats = listOf(
                AdminStat("Productos", "120"),
                AdminStat("Pedidos hoy", "8"),
                AdminStat("Clientes", "56"),
                AdminStat("Pendientes", "3")
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(stats) { stat ->
                    StatCard(title = stat.title, value = stat.value)
                }
            }
        }
    }
}

@Composable
private fun AdminHeaderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "Panel administrador",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestiona productos, pedidos y clientes",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(description, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun AddProductCard(onAddProduct: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onAddProduct() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Agregar producto", fontWeight = FontWeight.Bold)
                Text(
                    "Crear un nuevo producto en el catálogo",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

private data class AdminStat(val title: String, val value: String)

@Composable
private fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(title, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
