import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ProbarDigitacion {

    public static void main(String[] args) {
        // 1. Definir las rutas de prueba y las digitaciones
        // Formato de cada elemento: "cuerda,traste,dedo_izq,dedo_der"
        String[] digitaciones = {
            "c1,t1,di1,dd1+c2,t3,di3,dd1+c1,t0,di0,dd3",  // Cuerda 1, traste 1, dedo izq 1, dedo dcho 1 + ...
            "c2,t1,di1,dd2",  // Cuerda 2, traste 1, dedo izq 1, dedo dcho 2
            "c3,t3,di3,dd4",  // Cuerda 3, traste 3, dedo izq 3, dedo dcho 4
        };

        String archivoIn = "Python/acordes.xml";  // Partitura sin digitar
        String archivoOut = "Python/acordes_digitado.xml"; // Partitura digitada

        // 2. Construir el JSON manualmente para evitar dependencias de librerias externas
        String jsonInput = construirJson(digitaciones, archivoIn, archivoOut);

        System.out.println("--- ENVIANDO JSON A PYTHON ---");
        System.out.println(jsonInput);
        System.out.println("-------------------------------\n");

        // 3. Ejecutar el script de Python enviando el JSON por stdin
        try {
            // Usar "python" o "python3" segun la configuración del sistema
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
