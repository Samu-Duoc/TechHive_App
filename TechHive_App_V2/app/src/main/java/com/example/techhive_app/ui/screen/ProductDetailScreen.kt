package com.example.techhive_app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.techhive_app.data.local.cart.Cart
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.mock.MockColorVariants
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    productId: Long,
    productViewModel: ProductViewModel,
    onBack: () -> Unit = {}
) {
    val uiState by productViewModel.uiState.collectAsState()

    // Cargar producto por id
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

    // Variantes de color según el producto (mock por SKU)
    val colorVariants = remember(product.id) { MockColorVariants.forProduct(product) }

    // Color seleccionado (por defecto, el primero si existe)
    var selectedVariant by remember {
        mutableStateOf(colorVariants.firstOrNull())
    }

    // Imagen principal según color seleccionado (o la de la BD por defecto)
    val mainImageRes = run {
        val ctx = LocalContext.current
        if (selectedVariant != null) {
            val resId = ctx.resources.getIdentifier(
                selectedVariant!!.imageName,
                "drawable",
                ctx.packageName
            )
            if (resId != 0) resId else product.imageUrl
        } else {
            product.imageUrl
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Imagen + top bar simple
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            Image(
                painter = painterResource(id = mainImageRes),
                contentDescription = "Imagen de ${product.name}",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                IconButton(onClick = { /* Podrías ir al carrito o notificaciones */ }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre + categoría
        Text(
            text = product.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.category,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Precio
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Precio:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = formatPrice(product.price),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Stock
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Stock:", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "${product.stock} unidades",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // SKU
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "SKU:", style = MaterialTheme.typography.titleMedium)
            Text(text = product.sku, style = MaterialTheme.typography.titleMedium)
        }

        // -----------------------
        // Sección de COLORES
        // -----------------------
        if (colorVariants.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Colores disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(colorVariants) { variant ->
                    val isSelected = variant == selectedVariant

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedVariant = variant },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = variant.name.first().toString(),
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Descripción
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = product.description,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón añadir al carrito
        Button(
            onClick = {
                Cart.addItem(product)
                Toast.makeText(context, "Producto añadido al carrito", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Añadir al carrito", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}
