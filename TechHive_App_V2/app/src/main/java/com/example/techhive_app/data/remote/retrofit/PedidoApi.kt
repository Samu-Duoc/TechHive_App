package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.Pedido.ComprobantePagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.CrearPedidoPagoDTO
import com.example.techhive_app.data.remote.dto.Pedido.PedidoDTO
import com.example.techhive_app.data.remote.dto.Pedido.ActualizarEstadoPedidoDTO
import retrofit2.http.*


interface PedidoApi {

    @POST("pedidos/pagar")
    suspend fun pagar(@Body data: CrearPedidoPagoDTO): ComprobantePagoDTO

    @GET("pedidos")
    suspend fun listarTodos(): List<PedidoDTO>

    @GET("pedidos/usuario/{usuarioId}")
    suspend fun listarPorUsuario(@Path("usuarioId") usuarioId: Long): List<PedidoDTO>

    @GET("pedidos/{pedidoId}")
    suspend fun getById(@Path("pedidoId") pedidoId: String): PedidoDTO

    @PATCH("pedidos/{pedidoId}/estado")
    suspend fun cambiarEstado(
        @Path("pedidoId") pedidoId: String,
        @Body body: ActualizarEstadoPedidoDTO
    ): PedidoDTO

}


