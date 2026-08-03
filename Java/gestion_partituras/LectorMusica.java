package gestion_partituras;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LectorMusica {
	
	private final String RUTA_SCRIPT_LECTORNOTAS_PY = "/tfg_java/Python/LectorNotas.py";
	
    public void leerMidXml(String rutaPartitura) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", RUTA_SCRIPT_LECTORNOTAS_PY, rutaPartitura); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String jsonOutput = reader.readLine(); // JSON con la info de las notas/acordes

            System.out.println("JSON listo en Java: " + jsonOutput);

            // Parsear JSON con la librería Jackson si se necesita

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}