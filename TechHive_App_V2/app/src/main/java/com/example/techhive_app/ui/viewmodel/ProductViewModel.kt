package com.example.techhive_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


// Estado de la UI para la lista de productos
data class ProductUiState(
    val products: List<ProductEntity> = emptyList(), // Lista de productos
    val isLoading: Boolean = true, // Indicador de carga
    val error: String? = null // Mensaje de error
)

// ViewModel para la pantalla de productos
class ProductViewModel(private val repository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadProducts()
    }

    // Carga todos los productos desde el repositorio
    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) } // Inicia carga
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) } // Maneja errores
                .collect { products ->
                    _uiState.value = _uiState.value.copy(isLoading = false, products = products) // Carga exitosa
                }
        }
    }

    // Carga un producto específico por su ID
    fun loadProductById(productId: Long) {
        viewModelScope.launch {
            repository.getProductById(productId)
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { product ->
                    if (product != null) {
                        // Si el producto ya está en la lista, lo actualizamos. Si no, lo añadimos
                        val updatedProducts = _uiState.value.products.toMutableList()
                        val index = updatedProducts.indexOfFirst { it.id == productId }
                        if (index != -1) {
                            updatedProducts[index] = product
                        } else {
                            updatedProducts.add(product)
                        }
                        _uiState.value = _uiState.value.copy(isLoading = false, products = updatedProducts)
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Producto no encontrado")
                    }
                }
        }
    }

    //Insertar Productos
    fun insertProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            // Opcional: recargar lista (aunque tu Flow ya lo hace solo si vienes del DAO)
            // loadProducts()
        }
    }

    // Eliminar por ID
    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
            // Opcional: loadProducts()
        }
    }
}
