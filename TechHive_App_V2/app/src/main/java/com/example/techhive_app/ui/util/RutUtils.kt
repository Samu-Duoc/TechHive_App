package com.example.techhive_app.ui.util

fun normalizeRutForBackend(input: String): String {
    val raw = input.trim()
        .replace(".", "")
        .replace(" ", "")

    return if (raw.contains("-")) {
        raw
    } else if (raw.length >= 2) {
        raw.dropLast(1) + "-" + raw.last()
    } else {
        raw
    }
}
