package com.example.techhive_app.ui.screen.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.local.order.OrderManager
import com.example.techhive_app.ui.util.formatPrice


//Comporbante de pago (orden de compra)
@Composable
fun OrderConfirmationScreen(
    orderId: Long,
    onGoHome: () -> Unit,
    onGoHistory: () -> Unit
) {
    val orders by OrderManager.orders.collectAsState()
    val order = orders.find { it.id == orderId }

    if (order == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("No se encontró la orden")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onGoHome) {
                Text("Volver al inicio")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Compra realizada",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("N° de orden: #${order.id}")
        Text("Fecha: ${order.date}")
        Text("Estado: ${order.status}")

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Productos",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(order.items) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(item.product.name, fontWeight = FontWeight.SemiBold)
                            Text("Cantidad: ${item.quantity}")
                        }
                        Text(
                            formatPrice(item.product.price * item.quantity),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total pagado: ${formatPrice(order.total)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onGoHome,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al inicio")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onGoHistory,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver historial de compras")
        }
    }
}
