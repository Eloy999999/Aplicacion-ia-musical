package gestion_partituras;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LectorMusica {
	
	private final String RUTA_SCRIPT_LECTORNOTAS_PY = "Aplicacion-ia-musical/Python/LectorNotas.py";
    private final String RUTA_INTERPRETE = ".digitador/bin/python3";
	
    public void leerMidXml(String rutaPartitura) {
        try {
            ProcessBuilder pb = new ProcessBuilder(RUTA_INTERPRETE, RUTA_SCRIPT_LECTORNOTAS_PY, rutaPartitura); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String jsonOutput = reader.readLine(); // JSON con la info de las notas/acordes

            //while((jsonOutput = reader.readLine()) != null) {
            //    System.out.println("Salida de python: " + jsonOutput);
            //}

            System.out.println("JSON listo en Java: " + jsonOutput);
            // Parsear JSON con la librería Jackson si se necesita

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}