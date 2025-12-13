package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.util.base64ToBytes
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.common.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductGridScreen(
    productViewModel: ProductViewModel,
    onEditProduct: (Long) -> Unit,
    onAddProduct: () -> Unit // ✅ NUEVO: ir a crear producto
) {
    val uiState by productViewModel.uiState.collectAsState()
    val productsForGrid = uiState.products.distinctBy { it.sku }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Default.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(productsForGrid) { product ->
                AdminProductCard(
                    product = product,
                    onEdit = { onEditProduct(product.id) },
                    onDelete = { productViewModel.deleteRemote(product.id) }
                )
            }
        }
    }
}

@Composable
private fun AdminProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val bytes = remember(product.imageBase64) { base64ToBytes(product.imageBase64) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(bytes)
                        .crossfade(true)
                        .build(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentScale = ContentScale.Fit
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium)
                Text(formatPrice(product.price), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
