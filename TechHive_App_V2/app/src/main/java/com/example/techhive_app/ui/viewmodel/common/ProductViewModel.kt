package com.example.techhive_app.ui.viewmodel.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import com.example.techhive_app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

data class ProductUiState(
    val products: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState

    init {
        observeLocal()
        sync()
    }

    private fun observeLocal() {
        viewModelScope.launch {
            repository.observeAllProducts()
                .onStart { _uiState.value = _uiState.value.copy(isLoading = true, error = null) }
                .catch { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .collect { products ->
                    _uiState.value = _uiState.value.copy(isLoading = false, products = products)
                }
        }
    }

    fun sync() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.syncFromRemoteToLocal()
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                }
            // si sale ok, el observer local actualiza solo
        }
    }

    fun loadProductById(productId: Long) {
        // Tu detalle ya llama esto con LaunchedEffect :contentReference[oaicite:11]{index=11}
        // Como estamos observando local, esto puede quedarse como "no-op",
        // o puedes forzar un sync parcial si quieres.
    }

    // ----- ADMIN: CREAR / EDITAR / BORRAR REMOTO -----

    fun createRemote(dto: ProductRemoteDto, onOk: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.createRemote(dto)
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .onSuccess {
                    repository.syncFromRemoteToLocal()
                    onOk?.invoke()
                }
        }
    }

    fun updateRemote(id: Long, dto: ProductRemoteDto, onOk: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.updateRemote(id, dto)
                .onFailure { e -> _uiState.value = _uiState.value.copy(isLoading = false, error = e.message) }
                .onSuccess {
                    repository.syncFromRemoteToLocal()
                    onOk?.invoke()
                }
        }
    }

    fun deleteRemote(id: Long, onOk: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.deleteRemote(id)
                .onFailure { e -> _uiState.value = _uiState.value.copy(error = e.message) }
                .onSuccess {
                    repository.syncFromRemoteToLocal()
                    onOk?.invoke()
                }
        }
    }
}
