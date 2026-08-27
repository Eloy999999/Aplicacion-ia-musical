package gestion_partituras;

public class PartituraNoExisteException extends Exception {
    private final String nombrePartitura;

    public PartituraNoExisteException(String nombre) {
        nombrePartitura = nombre;
    }

    public String getNombrePartitura() {
        return nombrePartitura;
    }
}
