package gestion_partituras;

import java.io.File;
import java.nio.file.Path;

public class Partitura {
	private String nombre_partitura;
	private int tamanyo;
	private Path ruta_pdf;
//	private AudioMP3 audio;
	private Mi_MusicXML partitura_MusicXML;
	private Mi_Midi partitura_Midi;
	
	public Partitura(String nombre, Path pdf_path, Path musicxml) {
		nombre_partitura = nombre;
		ruta_pdf = pdf_path;
		partitura_MusicXML = new Mi_MusicXML(musicxml);
	}

	public Partitura(String nombre, Path pdf_path, Path musicxml, Path midi) {
		nombre_partitura = nombre;
		ruta_pdf = pdf_path;
		partitura_MusicXML = new Mi_MusicXML(musicxml);
		partitura_Midi = new Mi_Midi(midi);
	}

	public String getNombre_partitura() {
		return nombre_partitura;
	}

	public int getTamanyo() {
		return tamanyo;
	}

	public Path getRutaPDF() {
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
	
	public void setRutaPDF(Path nuevaRuta) {
		ruta_pdf = nuevaRuta;
	}
	
	
}
