package com.digitarra.gestion_partituras;

public class NombrePartituraEnUsoException extends Exception {
    private final String nombrePartitura;

    public NombrePartituraEnUsoException(String nombre) {
        nombrePartitura = nombre;
    }

    public String getNombrePartitura() {
        return nombrePartitura;
    }
}
