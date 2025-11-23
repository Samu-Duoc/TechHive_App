package com.example.techhive_app.ui.screen.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.ui.viewmodel.ProductViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class) ////Para que agarre el TOPBAR
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    productId: Long? = null,     // null = crear, no null = editar
    onFinished: () -> Unit
) {
    val uiState by productViewModel.uiState.collectAsState()
    val productToEdit = uiState.products.find { it.id == productId }

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var price by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "") }
    var stock by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "1") }

    // manejamos la imagen de la galería (por ahora solo preview, no se guarda en BD)
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (productId == null) "Nuevo producto" else "Editar producto")
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Preview de la imagen seleccionada (si hay)
            selectedImageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    galleryLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {
                Text("Elegir imagen de galería")
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val priceDouble = price.toDoubleOrNull() ?: 0.0
                    val stockInt = stock.toIntOrNull() ?: 0

                    // 👉 Usamos insertProduct tanto para crear como para editar (REPLACE)
                    val finalProduct: ProductEntity = if (productId == null) {
                        // CREAR
                        ProductEntity(
                            id = 0, // Room lo genera
                            name = name,
                            description = description,
                            price = priceDouble,
                            // por ahora usamos la imagen que tenga el edit (o 0 si es nuevo)
                            imageUrl = productToEdit?.imageUrl ?: 0,
                            stock = stockInt,
                            sku = "ADM-${System.currentTimeMillis()}",
                            category = category
                        )
                    } else {
                        // EDITAR → copiamos el existente, manteniendo id e imageUrl
                        productToEdit!!.copy(
                            name = name,
                            description = description,
                            price = priceDouble,
                            stock = stockInt,
                            category = category
                            // imageUrl se mantiene igual por ahora
                        )
                    }

                    productViewModel.insertProduct(finalProduct)

                    onFinished()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (productId == null) "Guardar producto" else "Guardar cambios")
            }
        }
    }
}
