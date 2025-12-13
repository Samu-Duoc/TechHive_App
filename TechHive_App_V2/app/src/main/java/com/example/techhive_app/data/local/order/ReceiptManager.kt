package com.example.techhive_app.data.local.order

import com.example.techhive_app.data.remote.dto.Pedido.ComprobantePagoDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ReceiptManager {
    private val _lastReceipt = MutableStateFlow<ComprobantePagoDTO?>(null)
    val lastReceipt: StateFlow<ComprobantePagoDTO?> = _lastReceipt

    fun setReceipt(receipt: ComprobantePagoDTO) {
        _lastReceipt.value = receipt
    }

    fun clear() {
        _lastReceipt.value = null
    }
}
