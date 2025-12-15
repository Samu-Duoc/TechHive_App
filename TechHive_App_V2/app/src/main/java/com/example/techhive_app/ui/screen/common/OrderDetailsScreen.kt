package com.example.techhive_app.ui.screen.common

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    LaunchedEffect(pedidoId) { viewModel.cargarDetalle(pedidoId) }

    val detalle = viewModel.detalle
    val context = LocalContext.current

    var selectedEstado by remember { mutableStateOf("") }
    LaunchedEffect(detalle) { detalle?.let { selectedEstado = it.estado } }

    val scrollState = rememberScrollState()

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
        },
        bottomBar = {
            if (mode == OrderViewerMode.ADMIN && detalle != null) {
                // Barra fija para acciones admin (siempre visible)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        AdminActionsCompact(
                            selectedEstado = selectedEstado,
                            onSelectedChange = { nuevo -> selectedEstado = nuevo }
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.cambiarEstado(detalle.pedidoId, selectedEstado)
                                    Toast.makeText(context, "Estado cambiado", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            ) {
                                Text("Confirmar")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        // Contenido scrolleable completo
        if (detalle == null) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isLoading) LinearProgressIndicator(Modifier.fillMaxWidth())
                else Text("No hay información del pedido")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding) // insets from Scaffold (incluye espacio por bottomBar)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (viewModel.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
            }

            viewModel.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            // Header reducido
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("Orden: ${detalle.pedidoId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Estado: ${detalle.estado}", fontSize = 14.sp)
                    Text("Fecha: ${detalle.fecha}", fontSize = 14.sp)
                    Text("Método de pago: ${detalle.metodoPago}", fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Total: ${formatPrice(detalle.total)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(Modifier.height(14.dp))
            Text("Productos", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))

            // Lista de productos dentro del scroll
            detalle.items.forEach { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text(item.nombreProducto, fontWeight = FontWeight.Bold)
                        Text("Cantidad: ${item.cantidad}")
                        Text("Precio: ${formatPrice(item.precioUnitario)}")
                        Text("Subtotal: ${formatPrice(item.subtotal)}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(Modifier.height(24.dp)) // espacio al final para que no choque con bottomBar
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminActionsCompact(
    selectedEstado: String,
    onSelectedChange: (String) -> Unit
) {
    val estados = listOf("CONFIRMADO", "PREPARANDO", "EN TRANSITO", "ENTREGADO", "CANCELADO")
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Acciones (Admin)", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))

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

        Spacer(Modifier.height(6.dp))
        Text("Estado actual: $selectedEstado", style = MaterialTheme.typography.bodySmall)
    }
}
