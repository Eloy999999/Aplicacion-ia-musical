package digitacion;

import gestion_partituras.LectorMusica;
import gestion_partituras.Partitura;


public class Digitador {
	
	
	public void digita(Partitura part) {
		LectorMusica lector = new LectorMusica();
		lector.leerMidXml(part.getPartitura_Midi().getRuta());
		
		
	}
}
