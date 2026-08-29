package com.example.digitarra.Java;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.example.digitarra.Java.digitacion.Digitador;
import com.example.digitarra.Java.digitacion.NotaDesconocidaException;
import com.example.digitarra.Java.gestion_partituras.Partitura;

import org.json.JSONException;

public class Main{
	
	private final static String archivo_midi_pruebas = "partituras/HotelCalifornia.mid";
	
    public static void main(String[] args){
//        LectorMusica lectorMusica = new LectorMusica();
//        lectorMusica.leerMidXml();
        try {
            Path path = Paths.get(archivo_midi_pruebas);

            if (Files.exists(path)) {
                //System.out.println("La ruta1 es válida y el archivo/directorio EXISTE.");
				Partitura part = new Partitura("PruebaMidi","","",archivo_midi_pruebas, false);
		

				Digitador digit = new Digitador();
				digit.digita(part);
    
            } else {
                System.out.println("La sintaxis de la ruta es válida, pero el archivo NO existe.");
            }

        } catch (InvalidPathException e) {
            // Se ejecuta si la cadena contiene caracteres no permitidos en el S.O.
            System.err.println("La cadena no es una ruta de archivo válida: " + e.getMessage());
        } catch (NotaDesconocidaException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
//    	String x = "si3,do5,mi3";
//    	String[] xs = x.split(",");
//    	System.out.println(xs.length);
//    	for(int i = 0; i < xs.length; i++) {
//    		System.out.println(xs[i]);
//    	}
    
    }
}
