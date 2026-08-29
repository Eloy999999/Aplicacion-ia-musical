package com.example.digitarra

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File

class GestorArchivos(private val context: Context) {

    fun obtenerNombreArchivo(uri: Uri): String {
        var resultado = "partitura_${System.currentTimeMillis()}" // Nombre unico por si falla la lectura de la uri
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                resultado = cursor.getString(nameIndex)
            }
        }
        return resultado
    }

    // Copia un archivo desde su Uri al almacenamiento interno de la app (filesDir).
    fun copiarArchivoAInterno(uri: Uri, nombreSalida: String): File {
        val destino = File(context.filesDir, nombreSalida)
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            destino.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return destino
    }
}