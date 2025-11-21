package com.example.techhive_app.ui.util

import java.text.NumberFormat
import java.util.Locale

// Formatea un precio de tipo Double a una cadena de texto
fun formatPrice(price: Double): String {
    // Usamos Locale("es", "CL") para asegurar el formato con punto como separador de miles
    val format = NumberFormat.getNumberInstance(Locale("es", "CL"))
    // Convertimos a Int para eliminar los decimales y luego formateamos.
    return "$${format.format(price.toInt())}"
}
