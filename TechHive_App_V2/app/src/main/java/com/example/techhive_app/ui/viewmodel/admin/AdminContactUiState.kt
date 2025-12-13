package com.example.techhive_app.ui.viewmodel.admin

import com.example.techhive_app.data.remote.dto.contacto.ContactResponse

data class AdminContactUiState(
    val isLoading: Boolean = false,
    val mensajes: List<ContactResponse> = emptyList(),
    val error: String? = null
)
