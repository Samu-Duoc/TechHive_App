package com.example.techhive_app.data.remote.retrofit

import com.example.techhive_app.data.remote.dto.contacto.ContactRequest
import com.example.techhive_app.data.remote.dto.contacto.ContactResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ContactApi {

    // Enviar mensaje desde la App
    @POST("contacto/guardar")
    suspend fun enviarContacto(
        @Body body: ContactRequest
    ): ContactResponse

    // Listar mensajes (solo ADMIN)
    @GET("contacto/listar")
    suspend fun listar(): List<ContactResponse>

    // Buscar por ID
    @GET("contacto/{id}")
    suspend fun getById(
        @Path("id") id: Long
    ): ContactResponse

    // Eliminar contacto (ADMIN)
    @DELETE("contacto/{id}")
    suspend fun eliminar(
        @Path("id") id: Long
    )
}

