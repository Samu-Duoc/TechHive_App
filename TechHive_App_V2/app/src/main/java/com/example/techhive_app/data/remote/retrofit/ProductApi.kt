package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.product.ProductCategoryDto
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import retrofit2.http.*

interface ProductApi {

    // ---- PRODUCTOS ----
    @GET("productos")
    suspend fun getProducts(): List<ProductRemoteDto>

    @GET("productos/{id}")
    suspend fun getProductById(@Path("id") id: Long): ProductRemoteDto

    @GET("productos/categoria/{nombre}")
    suspend fun getByCategory(@Path("nombre") category: String): List<ProductRemoteDto>

    @POST("productos")
    suspend fun create(@Body dto: ProductRemoteDto): ProductRemoteDto

    @PUT("productos/{id}")
    suspend fun update(@Path("id") id: Long, @Body dto: ProductRemoteDto): ProductRemoteDto

    @DELETE("productos/{id}")
    suspend fun delete(@Path("id") id: Long)

    // ---- CATEGORÍAS ----
    @GET("categorias")
    suspend fun getCategorias(): List<ProductCategoryDto>
}
