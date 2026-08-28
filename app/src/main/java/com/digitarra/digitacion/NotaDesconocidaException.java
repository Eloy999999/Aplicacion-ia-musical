package com.digitarra.digitacion;

public class NotaDesconocidaException extends Exception {
	private String notaInalcanzable;
	public NotaDesconocidaException(String nota) {
		notaInalcanzable = nota;
	}
	
	public String getNotaInalcanzable() {
		return notaInalcanzable;
	}
}
