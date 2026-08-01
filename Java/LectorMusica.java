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