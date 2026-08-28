package com.digitarra.gestion_partituras;

public class NombreColeccionEnUsoException extends Exception {
    private final String nombreColeccion;

    public NombreColeccionEnUsoException(String nombre) {
        nombreColeccion = nombre;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }
}
