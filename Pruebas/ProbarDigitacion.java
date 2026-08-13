import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ProbarDigitacion {

    public static void main(String[] args) {
        // 1. Definir las rutas de prueba y las digitaciones
        // Formato de cada elemento: "cuerda,traste,dedo_izq,dedo_der"
        String[] digitaciones = {
            "1,0,0,p",  // Cuerda 1, traste 0, sin dedo izq, pulgar
            "2,1,1,i",  // Cuerda 2, traste 1, dedo 1 izq, índice
            "3,2,2,m",  // Cuerda 3, traste 2, dedo 2 izq, medio
            "4,3,3,a",  // Cuerda 4, traste 3, dedo 3 izq, anular
            "5,5,4,p"   // Cuerda 5, traste 5 (V romano), dedo 4 izq, pulgar
        };

        String archivoIn = "Python/pruebamidi_digitado.xml";  // Partitura sin digitar
        String archivoOut = "Python/pruebamidi_digitado2.xml"; // Partitura digitada

        // 2. Construir el JSON manualmente para evitar dependencias de librerías externas
        String jsonInput = construirJson(digitaciones, archivoIn, archivoOut);

        System.out.println("--- ENVIANDO JSON A PYTHON ---");
        System.out.println(jsonInput);
        System.out.println("-------------------------------\n");

        // 3. Ejecutar el script de Python enviando el JSON por stdin
        try {
            // Usar "python" o "python3" según la configuración del sistema
            ProcessBuilder pb = new ProcessBuilder("python", "Python/DigitarPartitura.py");
            Process process = pb.start();

            // Escribir el JSON en el flujo de entrada (stdin) del proceso Python
            try (OutputStream os = process.getOutputStream()) {
                os.write(jsonInput.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            // Leer la salida estándar de Python (stdout)
            try (BufferedReader stdoutReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = stdoutReader.readLine()) != null) {
                    System.out.println("[PYTHON STDOUT] " + line);
                }
            }

            // Leer los mensajes de error de Python si los hubiera (stderr)
            try (BufferedReader stderrReader = new BufferedReader(
                    new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = stderrReader.readLine()) != null) {
                    System.err.println("[PYTHON STDERR] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println("\nProceso finalizado con código de salida: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper para construir la cadena JSON escapando adecuadamente las rutas.
     */
    private static String construirJson(String[] digitaciones, String archivoIn, String archivoOut) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"archivo_in\": \"").append(escaparRuta(archivoIn)).append("\",\n");
        sb.append("  \"archivo_out\": \"").append(escaparRuta(archivoOut)).append("\",\n");
        sb.append("  \"digitaciones\": [\n");

        for (int i = 0; i < digitaciones.length; i++) {
            sb.append("    \"").append(digitaciones[i]).append("\"");
            if (i < digitaciones.length - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }

    private static String escaparRuta(String ruta) {
        return ruta.replace("\\", "\\\\");
    }
}
