package com.example.techhive_app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.ProductViewModel


@OptIn(ExperimentalMaterial3Api::class) //Para que agarre el TOPBAR
@Composable
fun AdminProductGridScreen(
    productViewModel: ProductViewModel,
    onEditProduct: (Long) -> Unit
) {
    val uiState by productViewModel.uiState.collectAsState()
    val products = uiState.products

    //Para el panel admin, igual que en cliente: solo una carátula por SKU
    val productsForGrid = products.distinctBy { it.sku }


    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(productsForGrid) { product ->
            AdminProductCard(
                product = product,
                onEdit = { onEditProduct(product.id) },
                onDelete = { productViewModel.deleteProduct(product.id) } // implementa esto en el VM
            )
        }
    }
}

@Composable
private fun AdminProductCard(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar producto"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar producto"
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.name, style = MaterialTheme.typography.titleMedium)
                Text(text = formatPrice(product.price), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
