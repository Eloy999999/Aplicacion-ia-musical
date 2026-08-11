

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import digitacion.Digitador;
import gestion_partituras.Partitura;

public class Main{
	
	private final static String archivo_midi_pruebas = "partituras/HotelCalifornia.mid";
	
    public static void main(String[] args){
//        LectorMusica lectorMusica = new LectorMusica();
//        lectorMusica.leerMidXml();
        try {
            Path path = Paths.get(archivo_midi_pruebas);

            if (Files.exists(path)) {
                //System.out.println("La ruta1 es válida y el archivo/directorio EXISTE.");
				Partitura part = new Partitura(archivo_midi_pruebas);
		

				Digitador digit = new Digitador();
				digit.digita(part);
    
            } else {
                System.out.println("La sintaxis de la ruta es válida, pero el archivo NO existe.");
            }

        } catch (InvalidPathException e) {
            // Se ejecuta si la cadena contiene caracteres no permitidos en el S.O.
            System.err.println("La cadena no es una ruta de archivo válida: " + e.getMessage());
        }
    
    }
}
