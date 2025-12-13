package com.example.techhive_app.data.mapper

import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto

fun ProductRemoteDto.toEntity(): ProductEntity =
    ProductEntity(
        id = this.id ?: 0L,
        name = this.nombre,
        description = this.descripcion,
        price = this.precio,
        imageBase64 = this.imagenBase64,
        stock = this.stock,
        sku = this.sku,
        category = this.categoria
    )

fun ProductEntity.toRemoteDto(): ProductRemoteDto =
    ProductRemoteDto(
        id = this.id,
        nombre = this.name,
        descripcion = this.description,
        stock = this.stock,
        precio = this.price,
        estado = "ACTIVO",
        categoria = this.category,
        sku = this.sku,
        imagenBase64 = this.imageBase64
    )
