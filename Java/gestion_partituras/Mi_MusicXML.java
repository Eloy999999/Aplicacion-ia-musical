package com.digitarra.gestion_partituras;

import java.io.File;
import java.nio.file.Path;

public class Mi_MusicXML {
	private Path ruta_archivo;
	
	public Mi_MusicXML(Path rutaArchivo) {
		ruta_archivo = rutaArchivo;
	}
	
	
	
	
//	public void cambiar_nota(int pos_nota, NotaMusicXML nueva_nota) {
//
//	}
	
	
	//Funciones para editar la digitacion
	
	public void cambiar_cuerda(int pos_nota, int cuerda) {
		
	}
	
	public void cambiar_traste(int pos_nota, int traste) {
		
	}
	
	public void cambiar_dedo_I(int pos_nota, char dedo) {
		
	}
	
	public void cambiar_dedo_D(int pos_nota, int dedo) {
		
	}
	
	//Getter ruta
	public Path getRuta() {
		return ruta_archivo;
	}

	public void setRuta(Path rutaNueva) throws ArchivoNoSePudoBorrarException {
		File f = ruta_archivo.toFile();
		boolean seElimino = f.delete();
		if(!seElimino) {
			throw new ArchivoNoSePudoBorrarException(ruta_archivo.getFileName().toString());
		}
		ruta_archivo = rutaNueva;
	}
}
