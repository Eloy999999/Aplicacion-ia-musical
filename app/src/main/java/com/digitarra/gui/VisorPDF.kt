package com.digitarra.gui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.digitarra.gestion_partituras.Partitura
import java.io.File


class VisorPDF(private val context: Context) {
    fun visualizarPDF(partitura: Partitura) {
        // Obtenemos la ruta del PDF guardada en el objeto Partitura
        val pdfPath = partitura.rutaPDF.toString()

        if (pdfPath == null || pdfPath == "") {
            Toast.makeText(context, "No hay un archivo PDF asociado a esta partitura", Toast.LENGTH_SHORT).show()
            return
        }

        val file = File(pdfPath)

        if (!file.exists()) {
            Toast.makeText(context, "El archivo PDF no existe en el almacenamiento", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            // Intent para ver/visualizar el documento PDF
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context,
                "No hay ninguna aplicación instalada para abrir archivos PDF",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "Error al abrir el PDF: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}