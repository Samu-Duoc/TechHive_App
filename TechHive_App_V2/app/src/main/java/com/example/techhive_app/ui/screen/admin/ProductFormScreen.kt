package com.example.techhive_app.ui.screen.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.example.techhive_app.ui.viewmodel.common.ProductViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.techhive_app.data.remote.dto.product.ProductCategoryDto
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.ui.util.uriToDataImage
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import androidx.compose.ui.platform.LocalContext
import android.content.Intent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productViewModel: ProductViewModel,
    productId: Long? = null,
    onFinished: () -> Unit
) {
    val uiState by productViewModel.uiState.collectAsState()
    val productToEdit = uiState.products.find { it.id == productId }
    val ctx = LocalContext.current

    var name by remember { mutableStateOf(productToEdit?.name ?: "") }
    var price by remember { mutableStateOf(productToEdit?.price?.toString() ?: "") }
    var description by remember { mutableStateOf(productToEdit?.description ?: "") }
    var category by remember { mutableStateOf(productToEdit?.category ?: "") }
    var stock by remember { mutableStateOf(productToEdit?.stock?.toString() ?: "1") }

    var sku by remember { mutableStateOf(productToEdit?.sku ?: "") }
    var skuError by remember { mutableStateOf<String?>(null) }


    val productApi = remember { RemoteModule.productApi }

    var categories by remember { mutableStateOf<List<ProductCategoryDto>>(emptyList()) }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            categories = productApi.getCategorias()
            if (category.isBlank() && categories.isNotEmpty()) {
                category = categories.first().nombre
            }
        } catch (_: Exception) {
            categories = listOf(
                ProductCategoryDto(0, "Smartphones"),
                ProductCategoryDto(0, "Accesorios"),
                ProductCategoryDto(0, "Audio"),
                ProductCategoryDto(0, "Componentes"),
                ProductCategoryDto(0, "Computadores"),
                ProductCategoryDto(0, "Consolas"),
                ProductCategoryDto(0, "Periféricos")
            )

            if (category.isBlank()) {
                category = categories.first().nombre
            }
        }
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            ctx.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
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
                    fileLauncher.launch(arrayOf("image/*"))
                }
            ) {
                Text("Elegir imagen (Archivos / Drive)")
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
                value = sku,
                onValueChange = {
                    sku = it
                    skuError = null
                },
                label = { Text("SKU") },
                modifier = Modifier.fillMaxWidth(),
                isError = skuError != null,
                supportingText = {
                    if (skuError != null) Text(skuError!!)
                    else Text("Ej: SMT-APL-005 (debe ser único)")
                }
            )


            ExposedDropdownMenuBox(
                expanded = isCategoryMenuExpanded,
                onExpandedChange = { isCategoryMenuExpanded = !isCategoryMenuExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { },
                    label = { Text("Categoría") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = isCategoryMenuExpanded,
                    onDismissRequest = { isCategoryMenuExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                category = cat.nombre
                                isCategoryMenuExpanded = false
                            }
                        )
                    }
                }
            }

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
                    val priceDouble = price.replace(",", ".").toDoubleOrNull() ?: 0.0
                    val stockInt = stock.toIntOrNull() ?: 0

                    // VALIDACIONES
                    if (name.trim().isBlank()) {
                        return@Button
                    }

                    if (priceDouble <= 0) {
                        return@Button
                    }

                    if (stockInt <= 0) {
                        return@Button
                    }

                    val skuFinal = sku.trim()
                    if (skuFinal.isBlank()) {
                        skuError = "SKU obligatorio"
                        return@Button
                    }

                    val base64Image = selectedImageUri?.let { uri ->
                        uriToDataImage(ctx.contentResolver, uri)
                    }

                    val dto = ProductRemoteDto(
                        id = productId,
                        nombre = name,
                        descripcion = description,
                        precio = priceDouble,
                        stock = stockInt,
                        estado = "ACTIVO",
                        categoria = category,
                        sku = skuFinal,
                        imagenBase64 = base64Image
                    )

                    if (productId == null) {
                        productViewModel.createRemote(dto) { onFinished() }
                    } else {
                        productViewModel.updateRemote(productId, dto) { onFinished() }
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (productId == null) "Guardar producto" else "Guardar cambios")
            }

        }
    }
}
