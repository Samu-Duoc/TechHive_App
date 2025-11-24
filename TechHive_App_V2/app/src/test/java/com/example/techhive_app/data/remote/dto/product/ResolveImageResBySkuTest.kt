package com.example.techhive_app.data.remote.dto.product

import com.example.techhive_app.R
import org.junit.Assert.assertEquals
import org.junit.Test

class ResolveImageResBySkuTest {

    @Test
    fun `cuando el sku es null devuelve el logo por defecto`() {
        val result = resolveImageResBySku(null)
        assertEquals(R.drawable.logo, result)
    }

    @Test
    fun `cuando el sku no existe devuelve logo`() {
        val result = resolveImageResBySku("SKU-INEXISTENTE-99")
        assertEquals(R.drawable.logo, result)
    }

    @Test
    fun `sku PC-ASU-001 devuelve imagen correcta`() {
        val result = resolveImageResBySku("PC-ASU-001")
        assertEquals(R.drawable.asus_vivobook15, result)
    }
}
