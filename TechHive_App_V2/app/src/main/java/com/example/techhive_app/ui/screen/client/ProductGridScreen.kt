package com.example.techhive_app.ui.screen.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.ExperimentalLayoutApi


@OptIn (ExperimentalMaterial3Api::class)
@Composable
fun ProductGridScreen(
    productViewModel: ProductViewModel,
    onProductClick: (Long) -> Unit,
    initialCategory: String? = null      //filtro inicial opcional
) {
    val uiState by productViewModel.uiState.collectAsState()
    val products = uiState.products

    // categorías distintas de los productos
    val allCategories = remember(products) {
        products.map { it.category }
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
    }

    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // aplicar filtro por categoría (si hay)
    val filteredProducts = remember(products, selectedCategory) {
        val base = if (selectedCategory.isNullOrBlank()) products
        else products.filter { it.category.equals(selectedCategory, ignoreCase = true) }

        // solo una carátula por SKU
        base.distinctBy { it.sku }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---- FILTRO DE CATEGORÍAS ----
        if (allCategories.isNotEmpty()) {
            CategoryFilterRow(
                categories = allCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.error != null) {
            Text(
                text = "Error al cargar productos: ${uiState.error}",
                color = Color.Red
            )
        }


        // ---- GRID DE PRODUCTOS ----
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredProducts) { product ->
                ProductGridCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        // chip "Todas"
        FilterChip(
            selected = selectedCategory == null,
            onClick = { onCategorySelected(null) },
            label = { Text("Todas") },
            modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
        )

        categories.forEach { cat ->
            FilterChip(
                selected = selectedCategory.equals(cat, ignoreCase = true),
                onClick = { onCategorySelected(cat) },
                label = { Text(cat) },
                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Composable
fun ProductGridCard(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
