package com.example.techhive_app.ui.viewmodel.admin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.data.remote.dto.Pedido.ActualizarEstadoPedidoDTO
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import kotlinx.coroutines.launch

class AdminOrdersViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var orders by mutableStateOf<List<PedidoDTO>>(emptyList())
        private set

    fun cargarPedidos() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orders = RemoteModule.pedidoApi.listarTodos()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error al listar pedidos"
            } finally {
                isLoading = false
            }
        }
    }

    fun buscarPorId(pedidoId: String) {
        val id = pedidoId.trim()
        if (id.isBlank()) {
            errorMessage = "Ingresa un ID válido"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val p = RemoteModule.pedidoApi.getById(id)
                orders = listOf(p)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Pedido no encontrado"
            } finally {
                isLoading = false
            }
        }
    }

    fun cambiarEstado(pedidoId: String, nuevoEstado: String, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                RemoteModule.pedidoApi.cambiarEstado(pedidoId, ActualizarEstadoPedidoDTO(nuevoEstado))
                // refrescar lista completa después del cambio
                orders = RemoteModule.pedidoApi.listarTodos()
                onDone?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error al actualizar estado"
            } finally {
                isLoading = false
            }
        }
    }
}
