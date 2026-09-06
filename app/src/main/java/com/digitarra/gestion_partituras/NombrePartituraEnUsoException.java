package com.digitarra.gestion_partituras;

public class NombrePartituraEnUsoException extends Exception {
    private final String nombrePartitura;

    public NombrePartituraEnUsoException(String nombre) {
        super("El nombre de partitura \"" + nombre + "\" ya esta en uso");
        nombrePartitura = nombre;
    }

    public String getNombrePartitura() {
        return nombrePartitura;
    }
}
