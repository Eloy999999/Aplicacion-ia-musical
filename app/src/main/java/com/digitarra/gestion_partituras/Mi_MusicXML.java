package com.digitarra.gestion_partituras;

import java.io.File;
import java.nio.file.Path;

public class Mi_MusicXML {
	private Path ruta_archivo;
	
	public Mi_MusicXML(Path rutaArchivo) {
		ruta_archivo = rutaArchivo;
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
