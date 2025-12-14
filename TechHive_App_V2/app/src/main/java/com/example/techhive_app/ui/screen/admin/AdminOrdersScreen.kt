package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import com.example.techhive_app.ui.viewmodel.admin.AdminOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: AdminOrdersViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onOpenOrderDetails: (String) -> Unit = {}
) {
    // ✅ FIX: sin espacio en "EN TRANSITO"
    val estadosDisponibles = listOf("CONFIRMADO", "PREPARANDO", "EN TRANSITO", "ENTREGADO", "CANCELADO")

    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var showOnlyNuevos by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.cargarPedidos() }

    val ordersToShow = remember(viewModel.orders, selectedFilters, showOnlyNuevos) {
        val base = viewModel.orders
        when {
            showOnlyNuevos -> base.filter { it.estado == "CONFIRMADO" }
            selectedFilters.isNotEmpty() -> base.filter { it.estado in selectedFilters }
            else -> base
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Órdenes") }) }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Spacer(Modifier.height(10.dp))

            // --- Botones rápidos ---
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        selectedFilters = emptySet()
                        showOnlyNuevos = false
                        viewModel.cargarPedidos()
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Todos") }

                OutlinedButton(
                    onClick = {
                        selectedFilters = emptySet()
                        showOnlyNuevos = true
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Nuevos") }
            }

            Spacer(Modifier.height(10.dp))

            // --- Chips filtros ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                estadosDisponibles.forEach { estado ->
                    val selected = estado in selectedFilters
                    FilterChip(
                        selected = selected,
                        onClick = {
                            showOnlyNuevos = false
                            selectedFilters = if (selected) selectedFilters - estado else selectedFilters + estado
                        },
                        label = { Text(estado) },
                        modifier = Modifier.height(36.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.height(10.dp))
            }

            if (viewModel.isLoading) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ordersToShow) { pedido ->
                    AdminOrderCard(
                        pedido = pedido,
                        onVerDetalle = { onOpenOrderDetails(pedido.pedidoId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    pedido: PedidoDTO,
    onVerDetalle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            Text("Pedido: ${pedido.pedidoId}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Usuario: ${pedido.usuarioId}")
            Text("Total: $${pedido.total}")
            Text("Fecha: ${pedido.fecha}")
            Text("Estado: ${pedido.estado}")

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onVerDetalle,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalle")
            }
        }
    }
}
