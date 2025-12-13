package com.example.techhive_app.ui.screen.client

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.order.ReceiptManager
import com.example.techhive_app.data.remote.dto.Pedido.CrearPedidoPagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.ItemPedidoDTO
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.ui.util.formatPrice
import kotlinx.coroutines.launch
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    userId: Long,
    onBack: () -> Unit,
    onPaidNavigateTicket: () -> Unit
) {
    val cartItems by Cart.items.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val totalPrice = remember(cartItems) { cartItems.sumOf { it.product.price * it.quantity } }

    // Métodos ficticios
    val methods = listOf(
        "MASTERCARD ****5678",
        "VISA ****1122",
        "PAYPAL",
        "TRANSFERENCIA"
    )
    var selectedMethod by remember { mutableStateOf(methods.first()) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text("Resumen", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Items: ${cartItems.size}")
            Text("Total: ${formatPrice(totalPrice)}", fontWeight = FontWeight.SemiBold)

            Spacer(Modifier.height(16.dp))

            Text("Método de pago", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                methods.forEach { method ->
                    val selected = method == selectedMethod
                    Card(
                        onClick = { selectedMethod = method },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(14.dp)) {
                            Text(method, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (cartItems.isEmpty()) {
                        Toast.makeText(context, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        try {
                            val dto = CrearPedidoPagoDTO(
                                usuarioId = userId,
                                direccionId = "1",
                                metodoPago = selectedMethod,
                                total = totalPrice,
                                items = cartItems.map { cartItem ->
                                    ItemPedidoDTO(
                                        productoId = cartItem.product.id,
                                        nombreProducto = cartItem.product.name,
                                        cantidad = cartItem.quantity,
                                        precioUnitario = cartItem.product.price
                                    )
                                }
                            )

                            val comprobante = RemoteModule.pedidoApi.pagar(dto)

                            ReceiptManager.setReceipt(comprobante)
                            Cart.clearCart()

                            Toast.makeText(context, comprobante.mensaje, Toast.LENGTH_SHORT).show()
                            onPaidNavigateTicket()

                        } catch (e: HttpException) {
                            val msg = e.response()?.errorBody()?.string()
                                ?: "Error al pagar (HTTP ${e.code()})"
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Error al pagar: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text(if (isLoading) "Procesando..." else "Confirmar pago")
            }
        }
    }
}
