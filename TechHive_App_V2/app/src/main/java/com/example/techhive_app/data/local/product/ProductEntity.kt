package com.example.techhive_app.data.local.product

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: Long,

    val name: String,
    val description: String,
    val price: Double,

    val imageBase64: String?,
    val stock: Int,
    val sku: String,
    val category: String
)

