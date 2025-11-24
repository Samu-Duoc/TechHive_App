package com.example.techhive_app.data.repository

import com.example.techhive_app.R
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import com.example.techhive_app.data.remote.retrofit.ProductApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProductRepository(
    private val api: ProductApi   //ahora viene del microservicio
) {

    // ===== LISTAR TODOS =====
    fun getAllProducts(): Flow<List<ProductEntity>> = flow {
        val remoteList = api.getProductos()          // MS /productos
        val entities = remoteList.map { it.toEntity() }
        emit(entities)
    }

    // ===== OBTENER UNO POR ID =====
    fun getProductById(id: Long): Flow<ProductEntity?> = flow {
        val dto = api.getProductoById(id)           // MS /productos/{id}
        emit(dto.toEntity())
    }

    // ===== CREAR / ACTUALIZAR =====
    suspend fun insertProduct(product: ProductEntity) {
        val dto = product.toRemoteDto()

        if (product.id == 0L) {
            // Crear
            api.insertProducto(dto.copy(id = null))
        } else {
            // Actualizar
            api.updateProducto(product.id, dto)
        }
    }

    // ===== ELIMINAR =====
    suspend fun deleteProductById(id: Long) {
        api.deleteProducto(id)
    }

    // ==================== MAPEOS ====================

    // De DTO remoto -> Entity local (con drawable)
    private fun ProductRemoteDto.toEntity(): ProductEntity {
        return ProductEntity(
            id = this.id ?: 0L,
            name = this.nombre,
            description = this.descripcion,
            price = this.precio,
            imageUrl = resolveImageRes(this),  // 👈 aquí elegimos drawable
            stock = this.stock,
            sku = this.sku,
            category = this.categoria
        )
    }

    // De Entity local -> DTO remoto
    private fun ProductEntity.toRemoteDto(): ProductRemoteDto {
        return ProductRemoteDto(
            id = this.id,
            nombre = this.name,
            descripcion = this.description,
            precio = this.price,
            imagen = "",              // ⚠️ ignoramos por ahora (usas drawables locales)
            sku = this.sku,
            categoria = this.category,
            stock = this.stock
        )
    }

    // Elegimos la imagen según SKU o categoría
    private fun resolveImageRes(dto: ProductRemoteDto): Int {
        return when (dto.sku) {
            //Aquí  mapear 1 a 1 según tu seed de Room (AppDatabase)
            //PC
            "PC-ASU-001" -> R.drawable.asus_vivobook15
            "PC-APL-002" -> R.drawable.macbook_air_m1
            "PC-GAM-003" -> R.drawable.pc_ryzen5
            "PC-DEL-010" -> R.drawable.dell_inspiron

            //Componentes
            "CMP-RTX-011" -> R.drawable.rtx_4060
            "CMP-MOB-012" -> R.drawable.asus_b550m


            //ACCESORIOS
            "ACC-CAR-006" -> R.drawable.cargador_65w
            "ACC-LAN-007" -> R.drawable.cable_lan_cat6
            "ACC-USB-008" -> R.drawable.pendrive_kingston

            //Consolas
            "CON-PS4-009" -> R.drawable.ps4_slim
            "CON-STM-013" -> R.drawable.steam_deck


            //Audio
            "AUD-SON-014" -> R.drawable.sony_wh1000xm4
            "AUD-SBL-015" -> R.drawable.sound_blasterx_g6


            //smartphones
            "SP-SAM-001" -> R.drawable.samsung_galaxy_s21
            "SP-APL-002" -> R.drawable.iphone_12

            //Perifericos
            "PER-RED-016" -> R.drawable.teclado_redragon_k630
            "PER-COU-017" -> R.drawable.silla_cougar_armor
            "PER-COU-018" -> R.drawable.mouse


            // Fallback: si no matchea, usa un placeholder
            else -> R.drawable.logo
        }
    }
}
