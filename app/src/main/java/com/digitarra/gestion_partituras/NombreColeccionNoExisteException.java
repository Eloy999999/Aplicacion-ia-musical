package com.digitarra.gestion_partituras;

public class NombreColeccionNoExisteException extends Exception {
    private final String nombreColeccion;

    public NombreColeccionNoExisteException(String nombre) {
        super("La coleccion \"" + nombre + "\" no existe");
        nombreColeccion = nombre;
    }

    public String getNombreColeccion() {
        return nombreColeccion;
    }
}
