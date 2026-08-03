import digitacion.Digitador;
import gestion_partituras.LectorMusica;
import gestion_partituras.Partitura;

public class Main{
	
	private final static String archivo_midi_pruebas = "/tfg_java/partituras/HotelCalifornia.mid";
	
    public static void main(String[] args){
//        LectorMusica lectorMusica = new LectorMusica();
//        lectorMusica.leerMidXml();
        
    	

    	Partitura part = new Partitura(archivo_midi_pruebas);


    	Digitador digit = new Digitador();
    	digit.digita(part);


    }
}