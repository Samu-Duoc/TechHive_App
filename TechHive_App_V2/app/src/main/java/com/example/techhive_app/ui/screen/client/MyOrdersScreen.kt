package com.example.techhive_app.ui.screen.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import com.example.techhive_app.ui.viewmodel.client.MyOrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(
    usuarioId: Long,
    viewModel: MyOrdersViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onOpenOrderDetails: (pedidoId: String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(usuarioId) {
        viewModel.cargarPedidos(usuarioId)
    }

    val upcoming = remember(viewModel.orders) {
        viewModel.orders.filter { it.estado.uppercase() in listOf("CONFIRMADO", "PREPARANDO", "EN_TRANSITO") }
    }
    val previous = remember(viewModel.orders) {
        viewModel.orders.filter { it.estado.uppercase() in listOf("ENTREGADO", "CANCELADO") }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Orders") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Upcoming (${upcoming.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Previous (${previous.size})") }
                )
            }

            when {
                viewModel.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                viewModel.errorMessage != null -> {
                    Column(
                        Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(viewModel.errorMessage ?: "Error", color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.refrescar(usuarioId) }) {
                            Text("Reintentar")
                        }
                    }
                }

                else -> {
                    val list = if (selectedTab == 0) upcoming else previous

                    if (list.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No tienes pedidos aquí aún.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(list) { pedido ->
                                OrderCard(
                                    pedido = pedido,
                                    onDetails = { onOpenOrderDetails(pedido.pedidoId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    pedido: PedidoDTO,
    onDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(14.dp)) {

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = "Order Nº ${pedido.pedidoId.take(8)}",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(text = "Estado: ${pedido.estado}")
                }

                Button(onClick = onDetails) {
                    Text("Order Details")
                }
            }

            Spacer(Modifier.height(10.dp))

            Text("Total: $${pedido.total}")
            Text("Fecha: ${pedido.fecha}")
        }
    }
}
