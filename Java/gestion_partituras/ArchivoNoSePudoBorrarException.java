package gestion_partituras;

public class ArchivoNoSePudoBorrarException extends Exception {
    private final String archivoImposible;
    public ArchivoNoSePudoBorrarException(String nombreArchivo) {
        archivoImposible = nombreArchivo;
    }

    public String getArchivoImposible() {
        return archivoImposible;
    }
}
