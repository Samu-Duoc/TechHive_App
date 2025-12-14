package com.example.techhive_app.ui.viewmodel.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.remote.dto.Pedido.ItemDetalleDTO
import com.example.techhive_app.ui.util.formatPrice

enum class OrderViewerMode { CLIENT, ADMIN }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    pedidoId: String,
    mode: OrderViewerMode,
    viewModel: OrderDetailsViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {}
) {
    LaunchedEffect(pedidoId) {
        viewModel.cargarDetalle(pedidoId)
    }

    val detalle = viewModel.detalle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de orden") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {

            if (viewModel.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            if (viewModel.errorMessage != null) {
                Text(viewModel.errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(10.dp))
            }

            if (detalle == null) {
                if (!viewModel.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay detalle para mostrar.")
                    }
                }
                return@Column
            }

            // HEADER
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text("Orden: ${detalle.pedidoId}", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Estado: ${detalle.estado}")
                    Text("Fecha: ${detalle.fecha}")
                    Text("Método de pago: ${detalle.metodoPago}")
                    Spacer(Modifier.height(8.dp))
                    Text("Total: ${formatPrice(detalle.total)}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // ADMIN: controles extra (misma vista, pero “modo admin”)
            if (mode == OrderViewerMode.ADMIN) {
                AdminEstadoBox(
                    pedidoId = detalle.pedidoId,
                    estadoActual = detalle.estado,
                    onCambiarEstado = { nuevo ->
                        viewModel.cambiarEstado(detalle.pedidoId, nuevo)
                    }
                )
                Spacer(Modifier.height(12.dp))
            }

            Text("Productos", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(detalle.items) { item ->
                    ItemRow(item)
                }
            }
        }
    }
}

@Composable
private fun AdminEstadoBox(
    pedidoId: String,
    estadoActual: String,
    onCambiarEstado: (String) -> Unit
) {
    val estados = listOf("CONFIRMADO", "PREPARANDO", "EN_TRANSITO", "ENTRANSITO", "ENTREGADO", "CANCELADO")
    var expanded by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text("Acciones (Admin)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("ID: $pedidoId")
            Text("Estado actual: $estadoActual")

            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Cambiar estado")
                    Spacer(Modifier.width(6.dp))
                    Text("Cambiar estado")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                expanded = false
                                onCambiarEstado(estado)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemRow(item: ItemDetalleDTO) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp)) {
            Text(item.nombreProducto, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Cantidad: ${item.cantidad}")
            Text("Precio unitario: ${formatPrice(item.precioUnitario)}")
            Text("Subtotal: ${formatPrice(item.subtotal)}", fontWeight = FontWeight.SemiBold)
        }
    }
}
