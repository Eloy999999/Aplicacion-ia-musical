package gestion_partituras;

import java.nio.file.Path;

public class Mi_Midi {
	private Path ruta_archivo;
	
	
	public Mi_Midi(Path ruta) {
		ruta_archivo = ruta;
	}


	public Path getRuta() {
		return ruta_archivo;
	}
	
}
