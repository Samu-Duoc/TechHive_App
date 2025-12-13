package com.example.techhive_app.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techhive_app.data.remote.retrofitbuilder.RemoteModule
import com.example.techhive_app.ui.viewmodel.admin.AdminContactUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminContactViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdminContactUiState())
    val uiState: StateFlow<AdminContactUiState> = _uiState

    fun cargarMensajes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val data = RemoteModule.contactApi.listar()
                _uiState.update {
                    it.copy(isLoading = false, mensajes = data)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Error al cargar mensajes")
                }
            }
        }
    }

    fun buscarPorId(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val msg = RemoteModule.contactApi.getById(id)
                _uiState.update {
                    it.copy(isLoading = false, mensajes = listOf(msg))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Mensaje no encontrado")
                }
            }
        }
    }
}
