package com.example.techhive_app.data.local.product

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val description: String,
    val price: Double,

    // IMPORTANTE: drawable local, NO String, NO imageRes extra
    @DrawableRes val imageUrl: Int,

    val stock: Int,
    val sku: String,
    val category: String
)
