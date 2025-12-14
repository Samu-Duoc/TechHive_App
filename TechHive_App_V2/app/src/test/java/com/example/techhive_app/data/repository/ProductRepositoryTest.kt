package com.example.techhive_app.data.repository

import com.example.techhive_app.data.local.product.ProductDao
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import com.example.techhive_app.data.remote.retrofit.ProductApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProductRepositoryTest {

    // Clase ProductRemoteDto para poder crear una instancia
    private fun remoteDto(id: Long): ProductRemoteDto {
        return ProductRemoteDto(
            id = id,
            nombre = "Prod$id",
            descripcion = "Desc$id",
            stock = 10,
            precio = 9990.0,
            estado = "ACTIVO",
            categoria = "CAT",
            sku = "SKU-$id",
            imagenBase64 = null
        )
    }

    // Test de fetchRemote: 1.Verifica que el repositorio retorne Result.success cuando la API responde OK
    //                      2. Se mockea ProductApi para no depender del backend
    //                      3.Se espera una lista de productos

    @Test
    fun fetchRemote_devuelve_success_cuando_api_ok() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        val sample = listOf(remoteDto(1), remoteDto(2))
        coEvery { api.getProducts() } returns sample

        val result = repo.fetchRemote()

        assertTrue(result.isSuccess)
        assertEquals(2, result.getOrNull()!!.size)
        assertEquals("Prod1", result.getOrNull()!![0].nombre)
    }

    // Test de fetchRemote: 1.Verifica que el repositorio retorne Result.failure cuando la API falla
    //                      2. Se mockea ProductApi para no depender del backend
    //                      3. Se espera un Result.failure con el mensaje de error
    @Test
    fun fetchRemote_devuelve_failure_cuando_api_falla() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        coEvery { api.getProducts() } throws RuntimeException("Fallo")

        val result = repo.fetchRemote()

        assertTrue(result.isFailure)
        assertEquals("Fallo", result.exceptionOrNull()!!.message)
    }

    // Test de createRemote: 1.Verifica que el repositorio retorne Result.success cuando la API responde OK
    //                       2. Se mockea ProductApi para no depender del backend
    //                       3. Se espera un producto creado
    @Test
    fun createRemote_devuelve_success() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        val dto = remoteDto(0).copy(id = null)
        val created = remoteDto(10)

        coEvery { api.create(dto) } returns created

        val result = repo.createRemote(dto)

        assertTrue(result.isSuccess)
        assertEquals(10L, result.getOrNull()!!.id)
        coVerify(exactly = 1) { api.create(dto) }
    }


    // Test de updateRemote: 1.Verifica que el repositorio retorne Result.success cuando la API responde OK
    //                       2. Se mockea ProductApi para no depender del backend
    //                       3. Se espera un producto actualizado
    @Test
    fun updateRemote_devuelve_success() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        val dto = remoteDto(5)
        coEvery { api.update(5L, dto) } returns dto

        val result = repo.updateRemote(5L, dto)

        assertTrue(result.isSuccess)
        assertEquals(5L, result.getOrNull()!!.id)
        coVerify(exactly = 1) { api.update(5L, dto) }
    }

    // Test de deleteRemote: 1.Verifica que el repositorio retorne Result.success cuando la API responde OK
    //                       2. Se mockea ProductApi para no depender del backend
    //                       3. Se espera un producto eliminado
    @Test
    fun deleteRemote_devuelve_success_unit() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        coEvery { api.delete(3L) } returns Unit

        val result = repo.deleteRemote(3L)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { api.delete(3L) }
    }

    // Test de syncFromRemoteToLocal: 1.Verifica que el repositorio retorne Result.success cuando la API responde OK
    //                               2. Se mockea ProductApi para no depender del backend
    //                               3. Se espera una lista de productos
    @Test
    fun syncFromRemoteToLocal_inserta_en_dao_cada_producto_mapeado() = runBlocking {
        val api = mockk<ProductApi>()
        val dao = mockk<ProductDao>(relaxed = true)
        val repo = ProductRepository(api, dao)

        val remoteList = listOf(remoteDto(1), remoteDto(2))
        coEvery { api.getProducts() } returns remoteList

        val result = repo.syncFromRemoteToLocal()

        assertTrue(result.isSuccess)
        // se insertan 2 entidades
        coVerify(exactly = 2) { dao.insert(any<ProductEntity>()) }
    }
}
