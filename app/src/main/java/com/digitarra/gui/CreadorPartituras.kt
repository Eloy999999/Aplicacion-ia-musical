package com.digitarra.gui

import android.content.Context
import android.net.Uri
import com.digitarra.gestion_partituras.BibliotecaPartituras
import com.digitarra.gestion_partituras.EmbajadorMusic21Python
import com.digitarra.gestion_partituras.GeneradorPDF
import com.digitarra.gestion_partituras.NombrePartituraEnUsoException
import com.digitarra.gestion_partituras.Partitura
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Paths

class CreadorPartituras(private val context: Context) {

    private val fileHelper = GestorArchivos(context)

    suspend fun procesarYGuardarPartitura(
        uri: Uri,
        biblioteca: BibliotecaPartituras?,
        nombre: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {

            if(biblioteca?.nombrePartituraExiste(nombre) == true) {
                return@withContext Result.failure<Unit>(NombrePartituraEnUsoException(nombre))
            }

            val nombreOriginal = fileHelper.obtenerNombreArchivo(uri)
            val extension = nombreOriginal.substringAfterLast(".").lowercase()
            val nombreSinExtension = nombreOriginal.substringBeforeLast(".")

            val nombreSinEspacios = nombre.replace(" ", "")

            val (xmlPath, midiPath) = when (extension) {
                "xml", "musicxml" -> {
                    val embajador = EmbajadorMusic21Python(context)
                    val xmlGuardado = fileHelper.copiarArchivoAInterno(uri, "temp/$nombreOriginal")
                    val xmlConvertidoFile = File(context.filesDir, "MusicXML_Files/$nombreSinEspacios.xml")
                    embajador.convierteAMusicXML(xmlGuardado.toPath(), xmlConvertidoFile.toPath())
                    while(!xmlConvertidoFile.exists() || xmlConvertidoFile.length() == 0L);
                    Pair(xmlConvertidoFile.absolutePath, null)
                }
                "mid", "midi" -> {
                    val midiGuardado = fileHelper.copiarArchivoAInterno(uri, "temp/$nombreOriginal")
                    val xmlConvertidoFile = File(context.filesDir, "MusicXML_Files/$nombreSinEspacios.xml")
                    if (!xmlConvertidoFile.exists()) {
//                        xmlConvertidoFile.writeText("<!-- XML generado desde $nombreOriginal -->")
                        val emabaj = EmbajadorMusic21Python(context)
                        emabaj.convierteAMusicXML(midiGuardado.toPath(), xmlConvertidoFile.toPath())
                        while(!xmlConvertidoFile.exists() || xmlConvertidoFile.length() == 0L);
                    }
                    Pair(xmlConvertidoFile.absolutePath, midiGuardado.absolutePath)
                }
                else -> return@withContext Result.failure(IllegalArgumentException("Formato no válido"))
            }

            // Generar PDF
            val pdfFile = File(context.filesDir, "PDFs/$nombreSinEspacios.pdf")
            val generador = GeneradorPDF(context)

            val rutaPdfGenerada = generador.obtenerPDF(xmlPath, pdfFile.absolutePath)
            val rutaPDF = rutaPdfGenerada ?: pdfFile.absolutePath

            // Crear modelo y guardarsuccess
//            val nuevaPartitura = if (midiPath != null) {
//                Partitura(nombreSinExtension, Paths.get(xmlPath), Paths.get(rutaPDF), false)
//            } else {
//                Partitura(nombreSinExtension, rutaPDF, xmlPath, false)
//            }
            val nuevaPartitura = Partitura(nombre, Paths.get(rutaPDF), Paths.get(xmlPath), false)

            biblioteca?.insertaPartitura(nuevaPartitura)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
//        try {
//            biblioteca?.nuevaPartitura(uri)
//            Result.success(Unit)
//        } catch(e: Exception) {
//            Result.failure(e)
//        }
    }
}