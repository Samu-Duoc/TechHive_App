package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.product.ProductCategoryDto
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE

interface ProductApi {

    // ---------- CATEGORÍAS ----------
    @GET("categorias")
    suspend fun getCategorias(): List<ProductCategoryDto>

    // ---------- PRODUCTOS ----------

    // LISTAR TODOS LOS PRODUCTOS
    @GET("productos")
    suspend fun getProductos(): List<ProductRemoteDto>

    // OBTENER PRODUCTO POR ID
    @GET("productos/{id}")
    suspend fun getProductoById(@Path("id") id: Long): ProductRemoteDto

    // CREAR PRODUCTO
    @POST("productos")
    suspend fun insertProducto(@Body dto: ProductRemoteDto)

    // ACTUALIZAR PRODUCTO
    @PUT("productos/{id}")
    suspend fun updateProducto(
        @Path("id") id: Long,
        @Body dto: ProductRemoteDto
    )

    // ELIMINAR PRODUCTO
    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Long)
}
