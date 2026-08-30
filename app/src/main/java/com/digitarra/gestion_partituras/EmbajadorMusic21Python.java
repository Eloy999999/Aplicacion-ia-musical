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
	
	//private final String RUTA_SCRIPT_LECTORNOTAS_PY = "/home/drm/git/Aplicacion-ia-musical/Python/LectorNotas.py";
	private final String RUTA_SCRIPT_LECTORNOTAS_PY = "LectorNotas";
	private final String RUTA_SCRIPT_DIGITACION_PY = "DigitarPartitura";

	private final String RUTA_SCRIPT_CONVERSOR = "Conversor";
    private final String RUTA_INTERPRETE = "venv/bin/python3";
	private final String RUTA_LOG = "/home/drm/git/Aplicacion-ia-musical/Python/log.txt";

	private Python py;

	public EmbajadorMusic21Python(Context context) {
		if(!Python.isStarted()) {
			Python.start(new AndroidPlatform(context.getApplicationContext()));
		}
		py = Python.getInstance();
	}

    public JSONObject getNotas(Path rutaPartitura) throws JSONException {
//            ProcessBuilder pb = new ProcessBuilder(RUTA_INTERPRETE, RUTA_SCRIPT_LECTORNOTAS_PY, rutaPartitura.toString()); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver
//
//            pb.redirectErrorStream(true);
//
//            Process process = pb.start();
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//
//            StringBuilder jsonOut = new StringBuilder();
//
////            String jsonOutput = reader.readLine(); // JSON con la info de las notas/acordes
//
//            String linea;
//
//            while((linea = reader.readLine()) != null) {
////                System.out.println("Salida de python: " + jsonOutput);
//            	jsonOut.append(linea);
//            }
//
//
//            process.waitFor();
////            System.out.println("JSON listo en Java: " + jsonOutput);
//            // Parsear JSON con la librería Jackson si se necesita
//
//            return new JSONObject(jsonOut.toString());
        try (PyObject jsonNotas = py.getModule(RUTA_SCRIPT_LECTORNOTAS_PY).callAttr("json_notas", rutaPartitura.toString())) {
            return new JSONObject(jsonNotas.toString());
        }
    }
    
    public void digitaPartitura(JSONObject info_digitacion) throws InterruptedException, JSONException, IOException {
//    	BufferedWriter cartero = null;
//		File log = new File(RUTA_LOG);
//    	ProcessBuilder pb = new ProcessBuilder(RUTA_INTERPRETE, "-u", RUTA_SCRIPT_DIGITACION_PY);
//		pb.redirectOutput(log);
//		pb.redirectErrorStream(true);
////		pb.inheritIO();
//		//Se inicia el script python que espera por STDIN el JSON con la informacion de la digitacion.
//		Process procesoDigitador = pb.start();
//		cartero = new BufferedWriter(new OutputStreamWriter(procesoDigitador.getOutputStream()));
//		//Enviamos el json y un newline para que python solo tenga que usar readNextLine()
//		System.out.println("Java: procedo a enviar el json de la digitacion");
//		cartero.write(info_digitacion.toString(0));
//		cartero.newLine();
//		System.out.println("Java: ya envie el json de la digitacion");
//		cartero.flush();
////		cartero.close();
//		// Esperamos a que termine y vemos el codigo de retorno.
//		int codRet = procesoDigitador.waitFor();
//		if(codRet != 0) {
//			System.out.println("Error en el script python" + codRet);
//		}
    	PyObject salida = py.getModule(RUTA_SCRIPT_DIGITACION_PY).callAttr("digitarPartitura", info_digitacion.toString());
    	
    }

	public String convierteAMusicXML(Path rutaArchivoAux, Path pathXMLNuevo) {


		PyObject salida = py.getModule(RUTA_SCRIPT_CONVERSOR).callAttr("convierteAMusicXML", rutaArchivoAux.toString(), pathXMLNuevo.toString());

		return pathXMLNuevo.toString();
	}

	public void editaPartitura(Path rutaXML, JSONObject cambiosPartitura) {
		//TODO: Hay que hacer un script python que aplique los cambios contenidos en cambiosPartitura
	}
}