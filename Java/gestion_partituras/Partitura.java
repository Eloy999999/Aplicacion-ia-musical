package gestion_partituras;

public class Partitura {
	private String nombre_partitura;
	private int tamanyo;
	private String ruta_pdf;
//	private AudioMP3 audio;
	private Mi_MusicXML partitura_MusicXML;
	private Mi_Midi partitura_Midi;
	
	public Partitura(String nombre, String pdf_path, Mi_MusicXML musicxml) {
		nombre_partitura = nombre;
		ruta_pdf = pdf_path;
		partitura_MusicXML = musicxml;
	}

	public Partitura(String nombre, String pdf_path, Mi_MusicXML musicxml, Mi_Midi midi) {
		nombre_partitura = nombre;
		ruta_pdf = pdf_path;
		partitura_MusicXML = musicxml;
		partitura_Midi = midi;
	}

	public String getNombre_partitura() {
		return nombre_partitura;
	}

	public int getTamanyo() {
		return tamanyo;
	}

	public String getRutaPDF() {
		return ruta_pdf;
	}
//
//	public AudioMP3 getAudio() {
//		return audio;
//	}

	public Mi_MusicXML getPartitura_MusicXML() {
		return partitura_MusicXML;
	}

	public Mi_Midi getPartitura_Midi() {
		return partitura_Midi;
	}
	
	public void setNombre(String nombre) {
		nombre_partitura = nombre;
	}
	
	public void setRutaPDF(String nuevaRuta) {
		ruta_pdf = nuevaRuta;
	}

	public void eliminaArchivos() {
		
	}
	
	
}
