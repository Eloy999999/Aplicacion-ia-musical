package com.digitarra.gestion_partituras;

public class PartituraNoExisteException extends Exception {
    private final String nombrePartitura;

    public PartituraNoExisteException(String nombre) {
        super("La partitura \"" + nombre + "\" no existe");
        nombrePartitura = nombre;
    }

    public String getNombrePartitura() {
        return nombrePartitura;
    }
}
