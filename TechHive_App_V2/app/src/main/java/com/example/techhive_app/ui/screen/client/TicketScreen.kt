package com.example.techhive_app.ui.screen.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.local.order.ReceiptManager
import com.example.techhive_app.ui.util.formatPrice

@Composable
fun TicketScreen(
    onGoHome: () -> Unit,
    onGoHistory: () -> Unit
) {
    val receipt by ReceiptManager.lastReceipt.collectAsState()

    if (receipt == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay comprobante para mostrar")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("¡Gracias!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Tu pedido se está procesando.")

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("DETALLES DE PAGO", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Pedido: ${receipt!!.pedidoId}")
                Text("Fecha: ${receipt!!.fecha}")
                Text("Método: ${receipt!!.metodoPago}")
                Spacer(Modifier.height(10.dp))
                Text("Total pagado: ${formatPrice(receipt!!.total)}", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.weight(1f))

        Button(onClick = onGoHome, modifier = Modifier.fillMaxWidth()) {
            Text("Seguir comprando")
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onGoHistory, modifier = Modifier.fillMaxWidth()) {
            Text("Ver historial")
        }
    }
}
