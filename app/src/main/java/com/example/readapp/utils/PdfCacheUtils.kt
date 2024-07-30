package com.example.readapp.utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object PdfCacheUtils {
    private const val CACHE_DIR = "pdf_cache"

    fun getPdfFile(context: Context, url: String): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        val fileName = url.hashCode().toString() + ".pdf"
        return File(cacheDir, fileName)
    }

    fun savePdfToCache(context: Context, url: String, bytes: ByteArray) {
        val file = getPdfFile(context, url)
        try {
            val fos = FileOutputStream(file)
            fos.write(bytes)
            fos.close()
        } catch (e: IOException) {
            Log.e("PdfCacheUtils", "Error saving PDF to cache: ${e.message}")
        }
    }

    fun loadPdfFromCache(context: Context, url: String): ByteArray? {
        val file = getPdfFile(context, url)
        return if (file.exists()) {
            try {
                val fis = FileInputStream(file)
                val bytes = fis.readBytes()
                fis.close()
                bytes
            } catch (e: IOException) {
                Log.e("PdfCacheUtils", "Error loading PDF from cache: ${e.message}")
                null
            }
        } else {
            null
        }
    }
}