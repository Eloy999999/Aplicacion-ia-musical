/* 
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LectorMusica {
    public void leerMidXml() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "Python/LectorNotas.py", "Python/pruebamidi.mid"); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver

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
    */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode; // jackson (para parsear json)
import com.fasterxml.jackson.databind.ObjectMapper;

public class LectorMusica {
    public void leerMidXml() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", "Python/LectorNotas.py", "Python/pruebamidi.mid"); // Ejecutar python, archivo python (sacador de notas), midi/xml a ver
            
            // Redirigir errores de Python a la consola de Java para poder ver si falla el script
            pb.redirectErrorStream(true); 

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String jsonOutput = reader.readLine(); // JSON con la info de las notas/acordes

            int exitCode = process.waitFor(); // eesperar que termine el sccript de python

            if (exitCode == 0 && jsonOutput != null) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(jsonOutput); // String - JSON con la libreria Jackson

                String archivo = rootNode.get("archivo").asText(); // Ejemplo de extraccion de los datos del json
                int totalNotas = rootNode.get("total_notas").asInt();
                ArrayList<String> listaNotas = new ArrayList<String>();
                
                System.out.println("Archivo procesado: " + archivo);
                System.out.println("Total de notas: " + totalNotas);

                JsonNode notasArray = rootNode.get("notas");
                if (notasArray.isArray()) {
                    for (JsonNode nota : notasArray) {
                        listaNotas.add(nota.asText());
                    }
                }
                System.out.println(listaNotas.toString());
            } else {
                System.err.println("Error al ejecutar el script de Python. Código de salida: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}