package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import com.example.techhive_app.data.remote.dto.product.toEntity
import com.example.techhive_app.data.remote.retrofit.ProductApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository(
    private val api: ProductApi   // Microservicio
) {

    // ===== LISTAR TODOS =====
    fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        val remoteList = api.getProductos()                // MS /productos
        val entities = remoteList.map { it.toEntity() }    // 👈 USAS TU EXTENSION REAL
        emit(entities)
    }

    // ===== OBTENER UNO POR ID =====
    fun getProductById(id: Long): Flow<ProductEntity?> = flow {
        val dto = api.getProductoById(id)                  // MS /productos/{id}
        emit(dto.toEntity())
    }

    // ===== CREAR / ACTUALIZAR =====
    suspend fun insertProduct(product: ProductEntity) {
        val dto = product.toRemoteDto()

        if (product.id == 0L) {
            api.insertProducto(dto.copy(id = null))
        } else {
            api.updateProducto(product.id, dto)
        }
    }

    // ===== ELIMINAR =====
    suspend fun deleteProductById(id: Long) {
        api.deleteProducto(id)
    }


    // ==================== MAPEOS ====================
    // SOLO dejamos este: Entity → Remote DTO
    private fun ProductEntity.toRemoteDto(): ProductRemoteDto {
        return ProductRemoteDto(
            id = if (this.id == 0L) null else this.id,  // para crear/actualizar
            nombre = this.name,
            descripcion = this.description,
            precio = this.price,
            stock = this.stock,
            estado = "Nuevo", // o el estado que manejes desde la app
            categoria = this.category,
            disponibilidad = if (this.stock > 0) "Disponible" else "Agotado",
            sku = this.sku
        )
    }
}
