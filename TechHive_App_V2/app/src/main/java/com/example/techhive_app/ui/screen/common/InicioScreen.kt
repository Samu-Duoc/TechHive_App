package com.example.techhive_app.ui.screen.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.techhive_app.R
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.util.formatPrice
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll


// Modelo de las categorías
data class Category(val name: String, val icon: ImageVector)

// Pantalla de Cliente (muy probable también del admin)
@Composable
fun InicioScreen(
    productViewModel: ProductViewModel,
    onCategoryClick: (String) -> Unit,
    onViewAllProducts: () -> Unit,
    onProductClick: (Long) -> Unit,
    onContactClick: () -> Unit
) {

    val uiState by productViewModel.uiState.collectAsState()

    val categories = listOf(
        Category("Smartphones", Icons.Default.Smartphone),
        Category("Audio", Icons.Default.Headphones),
        Category("Computadores", Icons.Default.DesktopWindows),
        Category("Reacondicionados", Icons.Default.Laptop),
        Category("Accesorios", Icons.Default.Watch),
        Category("Componentes", Icons.Default.Memory),
        Category("Consolas", Icons.Default.Gamepad),
        Category("Periféricos", Icons.Default.Mouse)
    )

    val destacados: List<ProductEntity> = uiState.products.take(6)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .verticalScroll(rememberScrollState()) // Habilitar desplazamiento vertical
            .padding(top = 8.dp, bottom = 96.dp)
    ) {
        SearchBar()
        PromotionalBanner()
        CategoriesCarousel(categories = categories, onCategoryClick = onCategoryClick)

        // ---- Sección de destacados ----
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Text(
                    text = "Error al cargar productos: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }

            destacados.isNotEmpty() -> {
                FlashSaleSection(
                    products = destacados,
                    onViewAll = onViewAllProducts,
                    onProductClick = onProductClick
                )

                Spacer(Modifier.height(20.dp))

                // --- Contact Card debajo de destacados ---
                ContactCard(onClick = onContactClick)
            }
        }
    }
}



// BARRA DE BÚSQUEDA
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    OutlinedTextField(
        value = "",
        onValueChange = {},
        leadingIcon = { Icon(Icons.Default.Search, "Buscar") },
        placeholder = { Text("Buscar productos...") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp)
    )
}

// BANNER PROMOCIONAL
@Composable
fun PromotionalBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.banner),
                contentDescription = "Banner promocional",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "iPhone 16 Pro",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Extraordinary Visual & Power",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = { /* Acción */ }) {
                    Text("Compra Ahora")
                }
            }
        }
    }
}


// CARRUSEL DE CATEGORÍAS
@Composable
fun CategoriesCarousel(
    categories: List<Category>,
    onCategoryClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
    ) {
        Text(
            text = "Categorías",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(Modifier.height(16.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                CategoryCircleItem(category = category) {
                    onCategoryClick(category.name)
                }
            }
        }
    }
}


// ITEM DEL CARRUSEL
@Composable
fun CategoryCircleItem(
    category: Category,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.width(80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            tonalElevation = 2.dp,
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape),
            color = Color.White,
            shadowElevation = 4.dp,
            onClick = onClick
        ) {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = category.name,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = Color.Black,
            maxLines = 1
        )
    }
}


// SECCIÓN “FLASH SALE / DESTACADOS”
@Composable
fun FlashSaleSection(
    products: List<ProductEntity>,
    onViewAll: () -> Unit,
    onProductClick: (Long) -> Unit
) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ofertas destacadas",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = onViewAll) {
                Text("Ver todos")
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { product ->
                FlashProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }

        Spacer(Modifier.height(20.dp))

    }
}

@Composable
fun ContactCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "¿Deseas cotizar o conocer el estado de tu pedido?",
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text("Contáctanos y te responderemos lo antes posible.")
        }
    }
}


@Composable
fun FlashProductCard(
    product: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column {
            Image(
                painter = painterResource(id = product.imageUrl),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = formatPrice(product.price),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}