package com.example.gestor_empleados.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileLogger {
    private const val FILE_NAME = "app_logs.txt"

    fun logEvent(context: Context, category: String, description: String) {
        try {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val logEntry = "$timestamp | $category | $description\n"

            val file = File(context.filesDir, FILE_NAME)

            FileOutputStream(file, true).use { stream ->
                OutputStreamWriter(stream).use { writer ->
                    writer.write(logEntry)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLogFile(context: Context): File {
        return File(context.filesDir, FILE_NAME)
    }
}