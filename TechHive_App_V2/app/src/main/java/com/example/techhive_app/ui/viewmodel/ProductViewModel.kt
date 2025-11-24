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
    val isLoading: Boolean = true,                  // Indicador de carga
    val error: String? = null                       // Mensaje de error
)

// ViewModel para la pantalla de productos
class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .onStart {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { products ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        products = products
                    )
                }
        }
    }

    fun loadProductById(productId: Long) {
        viewModelScope.launch {
            repository.getProductById(productId)
                .onStart {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        error = null
                    )
                }
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { product ->
                    if (product != null) {
                        val updated = _uiState.value.products.toMutableList()
                        val index = updated.indexOfFirst { it.id == productId }
                        if (index != -1) updated[index] = product else updated.add(product)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            products = updated
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "Producto no encontrado"
                        )
                    }
                }
        }
    }

    fun insertProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            loadProducts()
        }
    }

    fun deleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
            loadProducts()
        }
    }
}
