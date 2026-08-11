package gestion_partituras;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;

public class EmbajadorMusic21Python {
	
	private final String RUTA_SCRIPT_LECTORNOTAS_PY = "Aplicacion-ia-musical/Python/LectorNotas.py";
    private final String RUTA_INTERPRETE = ".digitador/bin/python3";
	
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
    
    public JSONObject digitaPartitura(JSONObject info_digitacion) {
    	JSONObject infoNuevaPartitura = null;
    	String ruta_nuevo_archivo = null;
    	
    	//TODO: Hacer esto para comunicarse con python y que haga la digitacion
    	
    	return infoNuevaPartitura;
    }
}