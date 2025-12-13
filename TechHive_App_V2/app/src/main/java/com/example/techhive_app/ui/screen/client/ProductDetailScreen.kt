package com.example.techhive_app.ui.screen.client

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.mock.MockColorVariants
import com.example.techhive_app.ui.util.base64ToBytes
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    productId: Long,
    productViewModel: ProductViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by productViewModel.uiState.collectAsState()

    LaunchedEffect(productId) {
        productViewModel.loadProductById(productId)
    }

    val product = uiState.products.find { it.id == productId }

    when {
        uiState.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Cargando detalle del producto...")
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Error: ${uiState.error}")
            }
        }

        product != null -> {
            ProductDetailContent(
                product = product,
                onBack = onBack
            )
        }

        else -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Producto no encontrado")
            }
        }
    }
}

@Composable
private fun ProductDetailContent(
    product: ProductEntity,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // Variantes mock por SKU/nombre (si existen)
    val colorVariants = remember(product.id) { MockColorVariants.forProduct(product) }
    var selectedVariant by remember { mutableStateOf(colorVariants.firstOrNull()) }

    // ✅ bytes desde base64 (sirve para PNG/JPG)
    val imageBytes = remember(product.imageBase64) { base64ToBytes(product.imageBase64) }

    // Modelo:
    // - si variante existe y tiene drawable -> resId
    // - si no -> bytes base64
    val mainImageModel: Any? = run {
        val resId = selectedVariant?.let { variant ->
            context.resources.getIdentifier(
                variant.imageName,
                "drawable",
                context.packageName
            )
        } ?: 0

        if (resId != 0) {
            resId
        } else {
            ImageRequest.Builder(context)
                .data(imageBytes)
                .crossfade(true)
                .build()
        }
    }

    var isFavorite by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    val maxQuantity = if (product.stock > 0) product.stock else 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Imagen grande
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = mainImageModel,
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.9f),
                contentScale = ContentScale.Fit
            )

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }

                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito",
                        tint = if (isFavorite) Color.Red else Color.Black
                    )
                }
            }
        }

        // Tarjeta blanca
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = product.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatPrice(product.price),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Stock: ${product.stock}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cantidad
                Text(
                    text = "Cantidad",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { if (quantity > 1) quantity-- },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Remove, contentDescription = "Menos") }

                    Text(
                        text = quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedButton(
                        onClick = { if (quantity < maxQuantity) quantity++ },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(40.dp)
                    ) { Icon(Icons.Default.Add, contentDescription = "Más") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Colores (mock)
                if (colorVariants.isNotEmpty()) {
                    Text(
                        text = "Colores disponibles",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(colorVariants) { variant ->
                            val isSelected = variant == selectedVariant

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedVariant = variant }
                            ) {
                                Text(
                                    text = variant.name,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Descripción
                Text(
                    text = "Descripción",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = product.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.weight(1f))

                // Añadir al carrito
                Button(
                    onClick = {
                        Cart.addItem(product, quantity)
                        Toast.makeText(context, "Añadido $quantity al carrito", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text("Añadir al carrito")
                }
            }
        }
    }
}
