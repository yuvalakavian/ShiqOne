package com.example.shiqone.utils.extensions

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun Bitmap.toFile(context: Context, name: String): File {
    val file = File(context.cacheDir, "image_$name.jpg")
    FileOutputStream(file).use { stream ->
        compress(Bitmap.CompressFormat.JPEG, 100, stream)
    }
    return file
}