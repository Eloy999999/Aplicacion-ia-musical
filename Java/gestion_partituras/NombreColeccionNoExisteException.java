package gestion_partituras;

public class NombreColeccionNoExisteException extends Exception {
    private final String nombreColeccion;

    public NombreColeccionNoExisteException(String nombre) {
        nombreColeccion = nombre;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }
}
