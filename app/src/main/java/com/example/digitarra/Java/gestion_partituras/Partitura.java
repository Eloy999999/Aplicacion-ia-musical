package com.example.digitarra.Java.gestion_partituras;

public class Partitura {
	private String nombre_partitura;
	private int tamanyo;
	private String ruta_pdf;
	//  private AudioMP3 audio;
	private Mi_MusicXML partitura_MusicXML;
	private Mi_Midi partitura_Midi;
	private boolean digitada;

	// Constructor 1: Sin MIDI
	public Partitura(String nombre, String pdf_path, String musicxml, boolean digitada) {
		this.nombre_partitura = nombre;
		this.ruta_pdf = pdf_path;
		this.partitura_MusicXML = new Mi_MusicXML(musicxml);
		this.digitada = digitada;
	}

	// Constructor 2: Con MIDI
	public Partitura(String nombre, String pdf_path, String musicxml, String midi, boolean digitada) {
		this.nombre_partitura = nombre;
		this.ruta_pdf = pdf_path;
		this.partitura_MusicXML = new Mi_MusicXML(musicxml);
		this.partitura_Midi = new Mi_Midi(midi);
		this.digitada = digitada;
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

	public void setDigitada(boolean digitada) {
		this.digitada = digitada;
	}

	public boolean isDigitada() {
		return this.digitada;
	}
}