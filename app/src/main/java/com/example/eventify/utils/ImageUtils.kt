package com.example.eventify.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun saveImageLocally( context: Context, uri: Uri): String? {
    return try {
        val imagesDir = File(
            context.filesDir,
            "event_images"
        )
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }
        val fileName = "image_${System.currentTimeMillis()}.jpg"
        val file = File(
            imagesDir,
            fileName
        )
        context.contentResolver
            .openInputStream(uri)
            ?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
        file.absolutePath

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}