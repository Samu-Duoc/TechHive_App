package com.example.techhive_app.ui.util

import android.content.ContentResolver
import android.net.Uri
import android.util.Base64

fun uriToDataImage(
    contentResolver: ContentResolver,
    uri: Uri
): String {
    val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
    val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
        ?: throw IllegalArgumentException("No se pudo leer la imagen")

    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
    return "data:$mimeType;base64,$base64"
}
