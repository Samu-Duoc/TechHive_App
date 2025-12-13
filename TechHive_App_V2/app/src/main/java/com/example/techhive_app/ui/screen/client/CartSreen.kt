package com.example.techhive_app.ui.screen.client

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.cart.CartItem
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.util.toDataImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    userId: Long,
    onGoCheckout: () -> Unit
) {
    val items by Cart.items.collectAsState()

    val total = remember(items) {
        items.sumOf { it.product.price * it.quantity }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi carrito") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (items.isEmpty()) {
                EmptyCart()
                return@Column
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = {
                            Cart.increaseQuantity(item.product.id)
                        },
                        onDecrease = {
                            Cart.decreaseQuantity(item.product.id)
                            Cart.decreaseQuantity(item.product.id)
                        },
                        onRemove = { Cart.removeItem(item.product.id) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(formatPrice(total), fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    Button(
                        onClick = onGoCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Ir a Pagar")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCart() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(Modifier.height(8.dp))
            Text("Tu carrito está vacío", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("Agrega productos para continuar.")
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Card(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                AsyncImage(
                    model = toDataImage(item.product.imageBase64),
                    contentDescription = item.product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = formatPrice(item.product.price),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.width(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = onDecrease, enabled = item.quantity > 1) {
                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                }

                Text(
                    text = item.quantity.toString(),
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
