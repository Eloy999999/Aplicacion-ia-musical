package com.digitarra.gestion_partituras;

import android.content.Context;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;

import org.json.JSONException;
import org.json.JSONObject;

public class EmbajadorMusic21Python {

    private final String RUTA_SCRIPT_LECTORNOTAS_PY = "LectorNotas";
    private final String RUTA_SCRIPT_DIGITACION_PY = "DigitarPartitura";
    private final String RUTA_SCRIPT_CONVERSOR = "Conversor";

    private Python py;

    public EmbajadorMusic21Python(Context context) {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context.getApplicationContext()));
        }
        py = Python.getInstance();
    }

    // LO USA Digitador.java
    public JSONObject getNotas(Path rutaPartitura) throws JSONException {
        try (PyObject jsonNotas = py.getModule(RUTA_SCRIPT_LECTORNOTAS_PY)
                .callAttr("json_notas", rutaPartitura.toString())) {

            return new JSONObject(jsonNotas.toString());
        }
    }

    // LO USA BibliotecaPartituras.java
    public JSONObject getNotasDetalladas(Path rutaPartitura)
            throws JSONException {

        try (PyObject jsonNotas =
                     py.getModule(RUTA_SCRIPT_LECTORNOTAS_PY)
                             .callAttr(
                                     "json_notas_detalladas",
                                     rutaPartitura.toString()
                             )) {

            return new JSONObject(
                    jsonNotas.toString()
            );
        }
    }

    public void digitaPartitura(JSONObject info_digitacion)
            throws InterruptedException, JSONException, IOException {

        PyObject salida = py.getModule(RUTA_SCRIPT_DIGITACION_PY)
                .callAttr(
                        "digitarPartitura",
                        info_digitacion.toString()
                );
    }

    public String convierteAMusicXML(Path rutaArchivoAux, Path pathXMLNuevo) {

        PyObject salida = py.getModule(RUTA_SCRIPT_CONVERSOR)
                .callAttr(
                        "convierteAMusicXML",
                        rutaArchivoAux.toString(),
                        pathXMLNuevo.toString()
                );

        return pathXMLNuevo.toString();
    }

    public void editaPartitura(
            Path rutaXML,
            JSONObject cambiosPartitura) throws Exception {

        PyObject resultado =
                py.getModule("EditorPartituras")
                        .callAttr(
                                "editar_partitura",
                                rutaXML.toString(),
                                cambiosPartitura.toString()
                        );

        boolean correcto =
                resultado.toBoolean();

        if (!correcto) {
            throw new IOException(
                    "EditorPartituras.py no pudo guardar la partitura."
            );
        }
    }

    public JSONObject getNotasEdicion(Path rutaPartitura) throws JSONException {
        try (PyObject jsonNotas = py.getModule(RUTA_SCRIPT_LECTORNOTAS_PY)
                .callAttr("json_notas_edicion", rutaPartitura.toString())) {

            return new JSONObject(jsonNotas.toString());
        }
    }
}