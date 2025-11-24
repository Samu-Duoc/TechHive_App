package com.example.techhive_app.ui.screen.client

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.cart.CartItem
import com.example.techhive_app.data.local.order.OrderManager
import com.example.techhive_app.data.remote.dto.Pedido.CrearPedidoPagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.ItemPedidoDTO
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.ui.util.formatPrice
import kotlinx.coroutines.launch

@Composable
fun CartScreen(
    onCheckout: (Long) -> Unit = {}  // devuelve orderId
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
                        onIncrease = {
                            Cart.updateQuantity(item.product.id, item.quantity + 1)
                        },
                        onDecrease = {
                            Cart.updateQuantity(item.product.id, item.quantity - 1)
                        },
                        onRemove = {
                            Cart.removeItem(item.product.id)
                        }
                    )
                }
            }

            // Pie: total + botón
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
                        // snapshot del carrito ANTES de vaciarlo
                        val itemsSnapshot = cartItems.toList()

                        if (itemsSnapshot.isEmpty()) {
                            Toast.makeText(
                                context,
                                "No hay productos en el carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        // 1) Crear orden LOCAL (para historial + comprobante)
                        val orderId = OrderManager.createOrderFromCart()
                        if (orderId == -1L) {
                            Toast.makeText(
                                context,
                                "No se pudo crear la orden",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        Toast.makeText(
                            context,
                            "Orden creada #$orderId",
                            Toast.LENGTH_SHORT
                        ).show()

                        // 2) Llamar al microservicio Pedidos+Pagos en segundo plano
                        //Recordar el pedido funciona pero arroga error cuando paga, pero si sube el pedido a la base de datos. 
                        scope.launch {
                            try {
                                val dto = CrearPedidoPagoDTO(
                                    usuarioId = "1",        // TODO: reemplazar por el id real del usuario logueado
                                    direccionId = "1",      // TODO: id real de dirección
                                    metodoPago = "WEB",
                                    total = totalPrice,
                                    items = itemsSnapshot.map { cartItem ->
                                        ItemPedidoDTO(
                                            productoId = cartItem.product.id.toString(),
                                            nombreProducto = cartItem.product.name,
                                            cantidad = cartItem.quantity,
                                            precioUnitario = cartItem.product.price
                                        )
                                    }
                                )

                                val comprobante = RemoteModule.pedidoApi.pagar(dto)

                                Toast.makeText(
                                    context,
                                    comprobante.mensaje,
                                    Toast.LENGTH_SHORT
                                ).show()
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Error al registrar el pedido en el servidor",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        // 3) Navegar al comprobante (usando la orden LOCAL)
                        onCheckout(orderId)
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

            Image(
                painter = painterResource(id = item.product.imageUrl),
                contentDescription = item.product.name,
                modifier = Modifier.size(60.dp)
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
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar producto"
                )
            }
        }
    }
}
