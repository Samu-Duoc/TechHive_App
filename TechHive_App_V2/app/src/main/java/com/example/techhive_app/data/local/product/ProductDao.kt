package com.example.techhive_app.data.local.product

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getById(productId: Long): Flow<ProductEntity?>

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteById(productId: Long)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun count(): Int
}
