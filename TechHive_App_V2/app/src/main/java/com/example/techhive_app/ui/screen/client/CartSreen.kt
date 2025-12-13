package com.example.techhive_app.ui.screen.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.cart.CartItem
import com.example.techhive_app.data.local.order.OrderManager
import com.example.techhive_app.data.remote.dto.Pedido.CrearPedidoPagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.ItemPedidoDTO
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.util.toDataImage
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    userId: Long,
    onCheckout: (Long) -> Unit = {}
) {
    val cartItems by Cart.items.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {

        if (cartItems.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tu carrito está vacío", style = MaterialTheme.typography.titleLarge)
            }
        } else {

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { Cart.updateQuantity(item.product.id, item.quantity + 1) },
                        onDecrease = { Cart.updateQuantity(item.product.id, item.quantity - 1) },
                        onRemove = { Cart.removeItem(item.product.id) }
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                val totalPrice = cartItems.sumOf { it.product.price * it.quantity }

                Text(
                    text = "Total: ${formatPrice(totalPrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val itemsSnapshot = cartItems.toList()
                        if (itemsSnapshot.isEmpty()) {
                            Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        scope.launch {
                            try {
                                val dto = CrearPedidoPagoDTO(
                                    usuarioId = userId,
                                    direccionId = "1",
                                    metodoPago = "APP",
                                    total = totalPrice,
                                    items = itemsSnapshot.map { cartItem ->
                                        ItemPedidoDTO(
                                            productoId = cartItem.product.id,
                                            nombreProducto = cartItem.product.name,
                                            cantidad = cartItem.quantity,
                                            precioUnitario = cartItem.product.price
                                        )
                                    }
                                )

                                val comprobante = RemoteModule.pedidoApi.pagar(dto)

                                val orderId = OrderManager.createOrderFromCart()
                                Toast.makeText(context, comprobante.mensaje, Toast.LENGTH_SHORT).show()
                                onCheckout(orderId)

                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al procesar el pago: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar compra")
                }
            }
        }
    }
}

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = toDataImage(item.product.imageBase64),
                contentDescription = item.product.name,
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatPrice(item.product.price),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onDecrease) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onIncrease) {
                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                }
            }

            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar producto")
            }
        }
    }
}
