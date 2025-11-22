package com.example.techhive_app.data.mock

import com.example.techhive_app.data.local.product.ProductEntity

data class ColorVariant(
    val name: String,      // Ej: "Crema"
    val imageName: String  // Ej: "sony_wh1000xm4_crema" (nombre del drawable sin extensión)
)

object MockColorVariants {

    // Devuelve los colores según el SKU del producto
    fun forProduct(product: ProductEntity): List<ColorVariant> =
        when (product.sku) {

            // 🎧 Sony WH-1000XM4  -> sku = "AUD-SON-014"
            "AUD-SON-014" -> listOf(
                ColorVariant("Crema", "sony_wh1000xm4_crema"),
                ColorVariant("Negro", "sony_wh1000xm4_negro"),
                ColorVariant("Azul Noche", "sony_wh1000xm4_azul")
            )

            //iPhone 12 Reacondicionado -> sku = "SMT-APL-005"
            "SMT-APL-005" -> listOf(
                ColorVariant("Azul", "iphone12_azul"),
                ColorVariant("Blanco", "iphone12_blanco"),
                ColorVariant("Morado", "iphone12_morado")
            )

            // Samsung Galaxy S21 -> sku = "SMT-SAM-004"
            "SMT-SAM-004" -> listOf(
                ColorVariant("Morado", "samsung_galaxy_s21_morado"),
                ColorVariant("Negro", "samsung_galaxy_s21_negro")
            )

            //Silla Gamer Cougar Armor -> sku = "PER-COU-017"
            "PER-COU-017" -> listOf(
                ColorVariant("Naranja", "silla_cougar_armor_naranja"),
                ColorVariant("Negra", "silla_cougar_armor_negra"),
                ColorVariant("Rosada", "silla_cougar_armor_rosada")
            )

            else -> emptyList()
        }
}


