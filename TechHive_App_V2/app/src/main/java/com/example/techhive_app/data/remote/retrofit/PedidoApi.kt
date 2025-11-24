package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.Pedido.ComprobantePagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.CrearPedidoPagoDTO
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST


interface PedidoApi {

    @POST("/pedidos/pagar")
    suspend fun pagar(@Body data: CrearPedidoPagoDTO): ComprobantePagoDTO
}


