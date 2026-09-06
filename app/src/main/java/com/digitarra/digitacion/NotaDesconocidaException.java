package com.digitarra.digitacion;

public class NotaDesconocidaException extends Exception {
	private String notaInalcanzable;
	public NotaDesconocidaException(String nota) {
		super("La nota \"" + nota + "\" es desconocida o esta fuera de octava");
		notaInalcanzable = nota;
	}
	
	public String getNotaInalcanzable() {
		return notaInalcanzable;
	}
}
