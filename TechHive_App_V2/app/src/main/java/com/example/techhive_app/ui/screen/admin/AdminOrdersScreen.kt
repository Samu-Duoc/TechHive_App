package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import com.example.techhive_app.ui.viewmodel.admin.AdminOrdersViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: AdminOrdersViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // estados disponibles para filtrar
    val estadosDisponibles = listOf("CONFIRMADO", "PREPARANDO", "ENTRANSITO", "ENTREGADO", "CANCELADO")

    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var showOnlyNuevos by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarPedidos()
    }

    // lista a mostrar según filtros
    val ordersToShow = remember(viewModel.orders, selectedFilters, showOnlyNuevos) {
        val base = viewModel.orders
        when {
            showOnlyNuevos -> base.filter { it.estado == "CONFIRMADO" }
            selectedFilters.isNotEmpty() -> base.filter { it.estado in selectedFilters }
            else -> base
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Ordenes") }) }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(12.dp)
        ) {

            Spacer(Modifier.height(10.dp))

            // Row con botones Todos / Nuevos
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
                ) {
                    Text("Todos")
                }

                OutlinedButton(
                    onClick = {
                        selectedFilters = emptySet()
                        showOnlyNuevos = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Nuevos")
                }
            }

            Spacer(Modifier.height(10.dp))

            // Filtros por estado como chips (arriba) - fila scrollable horizontal
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
                        onChangeEstado = { estado ->
                            viewModel.cambiarEstado(pedido.pedidoId, estado)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminOrderCard(
    pedido: PedidoDTO,
    onChangeEstado: (String) -> Unit
) {
    val estados = listOf("CONFIRMADO", "PREPARANDO", "EN_TRANSITO", "ENTREGADO", "CANCELADO")
    var expanded by remember { mutableStateOf(false) }

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
            Text("Estado actual: ${pedido.estado}")

            Spacer(Modifier.height(10.dp))

            // botón pequeño para abrir menu de cambio de estado
            Row {
                TextButton(
                    onClick = { expanded = true }
                ) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Cambiar")
                    Spacer(Modifier.width(6.dp))
                    Text("Cambiar")
                }
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
                            onChangeEstado(estado)
                        }
                    )
                }
            }

        }
    }
}