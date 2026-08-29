package com.example.digitarra

import android.content.Context
import android.net.Uri
import com.example.digitarra.Java.gestion_partituras.BibliotecaPartituras
import com.example.digitarra.Java.gestion_partituras.GeneradorPDF
import com.example.digitarra.Java.gestion_partituras.Partitura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CreadorPartituras(private val context: Context) {

    private val fileHelper = GestorArchivos(context)

    suspend fun procesarYGuardarPartitura(
        uri: Uri,
        biblioteca: BibliotecaPartituras?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val nombreOriginal = fileHelper.obtenerNombreArchivo(uri)
            val extension = nombreOriginal.substringAfterLast(".").lowercase()
            val nombreSinExtension = nombreOriginal.substringBeforeLast(".")

            val (xmlPath, midiPath) = when (extension) {
                "xml", "musicxml" -> {
                    val xmlGuardado = fileHelper.copiarArchivoAInterno(uri, nombreOriginal)
                    Pair(xmlGuardado.absolutePath, null)
                }
                "mid", "midi" -> {
                    val midiGuardado = fileHelper.copiarArchivoAInterno(uri, nombreOriginal)
                    val xmlConvertidoFile = File(context.filesDir, "$nombreSinExtension.xml")
                    if (!xmlConvertidoFile.exists()) {
                        xmlConvertidoFile.writeText("<!-- XML generado desde $nombreOriginal -->")
                    }
                    Pair(xmlConvertidoFile.absolutePath, midiGuardado.absolutePath)
                }
                else -> return@withContext Result.failure(IllegalArgumentException("Formato no válido"))
            }

            // Generar PDF
            val pdfFile = File(context.filesDir, "$nombreSinExtension.pdf")
            val generador = GeneradorPDF(context)
            val rutaPdfGenerada = generador.ObtenerPDF(xmlPath, pdfFile.absolutePath)
            val rutaPDF = rutaPdfGenerada ?: pdfFile.absolutePath

            // Crear modelo y guardar
            val nuevaPartitura = if (midiPath != null) {
                Partitura(nombreSinExtension, rutaPDF, xmlPath, midiPath, false)
            } else {
                Partitura(nombreSinExtension, rutaPDF, xmlPath, false)
            }

            biblioteca?.insertaPartitura(nuevaPartitura)
            biblioteca?.guardarCambiosEnJson(context)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}