package com.digitarra.gestion_partituras;

public class ArchivoNoSePudoBorrarException extends Exception {
    private final String archivoImposible;
    public ArchivoNoSePudoBorrarException(String nombreArchivo) {
        super("El archivo \"" + nombreArchivo + "\" no se pudo borrar");
        archivoImposible = nombreArchivo;
    }

    public String getArchivoImposible() {
        return archivoImposible;
    }
}
