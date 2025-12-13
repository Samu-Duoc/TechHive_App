package com.example.techhive_app.ui.util

import android.util.Base64

fun base64ToBytes(base64OrDataUri: String?): ByteArray? {
    if (base64OrDataUri.isNullOrBlank()) return null

    val clean = base64OrDataUri.substringAfter("base64,", base64OrDataUri)

    return try {
        Base64.decode(clean, Base64.DEFAULT)
    } catch (_: IllegalArgumentException) {
        null
    }
}
