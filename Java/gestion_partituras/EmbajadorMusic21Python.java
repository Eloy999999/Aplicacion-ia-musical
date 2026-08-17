package gestion_partituras;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.json.JSONObject;

public class EmbajadorMusic21Python {
	
	private final String RUTA_SCRIPT_LECTORNOTAS_PY = "/home/drm/git/Aplicacion-ia-musical/Python/LectorNotas.py";
	private final String RUTA_SCRIPT_DIGITACION_PY = "/home/drm/git/Aplicacion-ia-musical/Python/DigitacionFinal.py";
    private final String RUTA_INTERPRETE = "venv/bin/python3";
	
    public JSONObject getNotas(String rutaPartitura) {
        try {
            ProcessBuilder pb = new ProcessBuilder(RUTA_INTERPRETE, RUTA_SCRIPT_LECTORNOTAS_PY, rutaPartitura); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            StringBuilder jsonOut = new StringBuilder();
            
//            String jsonOutput = reader.readLine(); // JSON con la info de las notas/acordes

            String linea;
            
            while((linea = reader.readLine()) != null) {
//                System.out.println("Salida de python: " + jsonOutput);
            	jsonOut.append(linea);
            }

            
            process.waitFor();
//            System.out.println("JSON listo en Java: " + jsonOutput);
            // Parsear JSON con la librería Jackson si se necesita
            
            return new JSONObject(jsonOut.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
		return null;
        
        
    }
    
    public void digitaPartitura(JSONObject info_digitacion) {
    	BufferedWriter cartero = null;
    	ProcessBuilder pb = new ProcessBuilder(RUTA_INTERPRETE, RUTA_SCRIPT_DIGITACION_PY);
    	try {
    		//Se inicia el script python que espera por STDIN el JSON con la informacion de la digitacion.
			Process procesoDigitador = pb.start();
			cartero = new BufferedWriter(new OutputStreamWriter(procesoDigitador.getOutputStream()));
			//Enviamos el json y un newline para que python solo tenga que usar readNextLine()
			cartero.write(info_digitacion.toString());
			cartero.newLine();
			cartero.flush();
			// Esperamos a que termine y vemos el codigo de retorno.
			int codRet = procesoDigitador.waitFor();
			if(codRet != 0) {
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
    	
    	
    }
}