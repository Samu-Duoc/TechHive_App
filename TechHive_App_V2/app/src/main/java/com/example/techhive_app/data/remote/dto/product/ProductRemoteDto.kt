package com.example.techhive_app.data.remote.dto.product

import com.example.techhive_app.R
import com.example.techhive_app.data.local.product.ProductEntity

data class ProductRemoteDto(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val estado: String,
    val categoria: String,
    val disponibilidad: String,
    val sku: String
)


// ----------------- REMOTE DTO → ENTITY -----------------

fun ProductRemoteDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id ?: 0L,
        name = nombre,
        description = descripcion,
        price = precio,
        stock = stock,
        sku = sku,
        category = categoria,
        imageUrl = resolveImageResBySku(sku)
    )
}


// ----------------- SKU → DRAWABLE -----------------

fun resolveImageResBySku(sku: String?): Int {
    if (sku.isNullOrBlank()) return R.drawable.logo

    return when (sku.trim().uppercase()) {

        // 1. COMPUTADORES / LAPTOPS
        "PC-ASU-001" -> R.drawable.asus_vivobook15
        "PC-APL-002" -> R.drawable.macbook_air_m1
        "PC-APL-003" -> R.drawable.pc_ryzen5
        "PC-DEL-010" -> R.drawable.dell_inspiron

        // 2. SMARTPHONES
        "SMT-SAM-004" -> R.drawable.samsung_galaxy_s21
        "SMT-SAM-005" -> R.drawable.iphone_12

        // 3. ACCESORIOS
        "ACC-CAR-006" -> R.drawable.cargador_65w
        "ACC-LAN-007" -> R.drawable.cable_lan_cat6
        "ACC-USB-008" -> R.drawable.pendrive_kingston

        // 4. CONSOLAS
        "CON-PS4-009" -> R.drawable.ps4_slim
        "CON-STM-013" -> R.drawable.steam_deck

        // 5. COMPONENTES
        "CMP-RTX-011" -> R.drawable.rtx_4060
        "CMP-MOB-012" -> R.drawable.asus_b550m

        // 6. AUDIO
        "AUD-SON-014" -> R.drawable.sony_wh1000xm4
        "AUD-SBL-015" -> R.drawable.sound_blasterx_g6

        // 7. PERIFÉRICOS
        "PER-RED-016" -> R.drawable.teclado_redragon_k630
        "PER-COU-017" -> R.drawable.silla_cougar_armor
        "PER-COU-018" -> R.drawable.mouse

        else -> R.drawable.logo
    }
}


