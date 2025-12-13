package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.product.ProductDao
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.mapper.toEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import com.example.techhive_app.data.remote.retrofit.ProductApi
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val api: ProductApi,
    private val dao: ProductDao
) {
    // ---- LOCAL (UI) ----
    fun observeAllProducts(): Flow<List<ProductEntity>> = dao.getAll()
    fun observeProductById(productId: Long): Flow<ProductEntity?> = dao.getById(productId)

    suspend fun upsertLocal(product: ProductEntity) = dao.insert(product)
    suspend fun deleteLocal(productId: Long) = dao.deleteById(productId)

    // ---- REMOTE ----
    suspend fun fetchRemote(): Result<List<ProductRemoteDto>> = try {
        Result.success(api.getProducts())
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun createRemote(dto: ProductRemoteDto): Result<ProductRemoteDto> = try {
        Result.success(api.create(dto))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateRemote(id: Long, dto: ProductRemoteDto): Result<ProductRemoteDto> = try {
        Result.success(api.update(id, dto))
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteRemote(id: Long): Result<Unit> = try {
        api.delete(id)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // ---- SYNC ----
    suspend fun syncFromRemoteToLocal(): Result<Unit> {
        return fetchRemote().mapCatching { remoteList ->
            remoteList.forEach { dao.insert(it.toEntity()) }
        }
    }
}
