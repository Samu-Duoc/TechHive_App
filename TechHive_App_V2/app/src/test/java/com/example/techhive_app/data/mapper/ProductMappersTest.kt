package com.example.techhive_app.data.mapper

import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.remote.dto.product.ProductRemoteDto
import org.junit.Assert.*
import org.junit.Test

class ProductMappersTest {


    //Test de ProductRemoteDto a ProductEntity: 1.mapea los campos correctamente
    //                                          2.deja id en 0 si es null
    //                                          3.setea estado en ACTIVO

    @Test
    fun toEntity_mapea_campos_correctamente() {
        val dto = ProductRemoteDto(
            id = 4L,
            nombre = "Mouse",
            descripcion = "Gamer",
            stock = 5,
            precio = 9990.0,
            estado = "ACTIVO",
            categoria = "Periféricos",
            sku = "MOU-001",
            imagenBase64 = "base64"
        )

        val entity = dto.toEntity()

        assertEquals(4L, entity.id)
        assertEquals("Mouse", entity.name)
        assertEquals("Gamer", entity.description)
        assertEquals(9990.0, entity.price, 0.0)
        assertEquals("base64", entity.imageBase64)
        assertEquals(5, entity.stock)
        assertEquals("MOU-001", entity.sku)
        assertEquals("Periféricos", entity.category)
    }


    @Test
    fun toEntity_si_id_null_deja_id_en_0() {
        val dto = ProductRemoteDto(
            id = null,
            nombre = "Teclado",
            descripcion = "Mecánico",
            stock = 3,
            precio = 19990.0,
            estado = "ACTIVO",
            categoria = "Periféricos",
            sku = "KEY-001",
            imagenBase64 = null
        )

        val entity = dto.toEntity()
        assertEquals(0L, entity.id)
    }

    @Test
    fun toRemoteDto_mapea_campos_correctamente_y_estado_activo() {
        val entity = ProductEntity(
            id = 4L,
            name = "Monitor",
            description = "24 pulgadas",
            price = 120000.0,
            imageBase64 = null,
            stock = 2,
            sku = "MON-007",
            category = "Periféricos"
        )

        val dto = entity.toRemoteDto()

        assertEquals(4L, dto.id)
        assertEquals("Monitor", dto.nombre)
        assertEquals("24 pulgadas", dto.descripcion)
        assertEquals(2, dto.stock)
        assertEquals(120000.0, dto.precio, 0.0)
        assertEquals("ACTIVO", dto.estado)
        assertEquals("Periféricos", dto.categoria)
        assertEquals("MON-007", dto.sku)
        assertNull(dto.imagenBase64)
    }
}
