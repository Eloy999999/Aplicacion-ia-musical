package com.example.digitarra.Java.digitacion;

public class NotaDesconocidaException extends Exception {
	private String notaInalcanzable;
	public NotaDesconocidaException(String nota) {
		notaInalcanzable = nota;
	}
	
	public String getNotaInalcanzable() {
		return notaInalcanzable;
	}
}
