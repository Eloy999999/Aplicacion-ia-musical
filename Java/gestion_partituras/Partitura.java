package gestion_partituras;

public class Partitura {
	private String nombre_partitura;
	private int tamanyo;
	private Pdf vista_partitura;
	private AudioMP3 audio;
	private Mi_MusicXML partitura_MusicXML;
	private Mi_Midi partitura_Midi;
	
	public Partitura(String nombre) {
		nombre_partitura = nombre;
	}

	public String getNombre_partitura() {
		return nombre_partitura;
	}

	public int getTamanyo() {
		return tamanyo;
	}

	public Pdf getVista_partitura() {
		return vista_partitura;
	}

	public AudioMP3 getAudio() {
		return audio;
	}

	public Mi_MusicXML getPartitura_MusicXML() {
		return partitura_MusicXML;
	}

	public Mi_Midi getPartitura_Midi() {
		return partitura_Midi;
	}
	
	
}
