package com.example.techhive_app.ui.viewmodel.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.remote.dto.Pedido.ActualizarEstadoPedidoDTO
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDetalleDTO
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import kotlinx.coroutines.launch

class OrderDetailsViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var detalle by mutableStateOf<PedidoDetalleDTO?>(null)
        private set

    fun cargarDetalle(pedidoId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                detalle = RemoteModule.pedidoApi.getDetalle(pedidoId)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error al cargar detalle"
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
                // recargar para reflejar estado nuevo
                detalle = RemoteModule.pedidoApi.getDetalle(pedidoId)
                onDone?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error al cambiar estado"
            } finally {
                isLoading = false
            }
        }
    }
}
