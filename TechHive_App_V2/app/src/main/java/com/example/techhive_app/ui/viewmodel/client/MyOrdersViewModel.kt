package com.example.techhive_app.ui.viewmodel.client

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import kotlinx.coroutines.launch

class MyOrdersViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var orders by mutableStateOf<List<PedidoDTO>>(emptyList())
        private set

    fun cargarPedidos(usuarioId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orders = RemoteModule.pedidoApi.listarPorUsuario(usuarioId)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.message ?: "Error al cargar pedidos"
            } finally {
                isLoading = false
            }
        }
    }

    fun refrescar(usuarioId: Long) = cargarPedidos(usuarioId)
}
