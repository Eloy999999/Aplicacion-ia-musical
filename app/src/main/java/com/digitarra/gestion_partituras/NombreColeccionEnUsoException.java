package com.digitarra.gestion_partituras;

public class NombreColeccionEnUsoException extends Exception {
    private final String nombreColeccion;

    public NombreColeccionEnUsoException(String nombre) {
        super("El nombre de coleccion \"" + nombre + "\" ya esta en uso");
        nombreColeccion = nombre;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }
}
