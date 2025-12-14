package com.example.techhive_app.ui.screen.common

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.common.OrderDetailsViewModel
import com.example.techhive_app.ui.viewmodel.common.OrderViewerMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    pedidoId: String,
    mode: OrderViewerMode,
    onBack: () -> Unit,
    viewModel: OrderDetailsViewModel = viewModel()
) {
    LaunchedEffect(pedidoId) {
        viewModel.cargarDetalle(pedidoId)
    }

    val detalle = viewModel.detalle
    val context = LocalContext.current

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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {

            if (viewModel.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            viewModel.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            if (detalle == null) {
                if (!viewModel.isLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay información del pedido")
                    }
                }
                return@Column
            }

            // ===== HEADER =====
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Orden: ${detalle.pedidoId}", fontWeight = FontWeight.Bold)
                    Text("Estado: ${detalle.estado}")
                    Text("Fecha: ${detalle.fecha}")
                    Text("Método de pago: ${detalle.metodoPago}")
                    Spacer(Modifier.height(8.dp))
                    Text("Total: ${formatPrice(detalle.total)}", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            // ===== PRODUCTOS (ocupan menos espacio para subir las acciones) =====
            Text("Productos", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            // se reduce el peso para que las acciones queden más arriba
            LazyColumn(
                modifier = Modifier
                    .weight(0.78f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(detalle.items) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(14.dp)) {
                            Text(item.nombreProducto, fontWeight = FontWeight.Bold)
                            Text("Cantidad: ${item.cantidad}")
                            Text("Precio: ${formatPrice(item.precioUnitario)}")
                            Text("Subtotal: ${formatPrice(item.subtotal)}", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            // estado seleccionado en memoria local para confirmar después
            var selectedEstado by remember(detalle.estado) { mutableStateOf(detalle.estado) }

            // ===== ADMIN ACTIONS (ahora más arriba) =====
            if (mode == OrderViewerMode.ADMIN) {
                AdminActionsDropdown(
                    selectedEstado = selectedEstado,
                    onSelectedChange = { nuevo -> selectedEstado = nuevo }
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            // aplicar cambio de estado, mostrar toast y volver
                            viewModel.cambiarEstado(detalle.pedidoId, selectedEstado)
                            Toast.makeText(context, "Estado cambiado", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    ) {
                        Text("Confirmar")
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminActionsDropdown(
    selectedEstado: String,
    onSelectedChange: (String) -> Unit
) {
    val estados = listOf("CONFIRMADO", "PREPARANDO", "EN TRANSITO", "ENTREGADO", "CANCELADO")

    var expanded by remember { mutableStateOf(false) }

    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Acciones (Admin)", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedEstado,
                    onValueChange = {},
                    label = { Text("Cambiar estado") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    estados.forEach { estado ->
                        DropdownMenuItem(
                            text = { Text(estado) },
                            onClick = {
                                onSelectedChange(estado)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Estado actual: $selectedEstado")
        }
    }
}
