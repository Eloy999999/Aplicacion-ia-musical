package com.example.digitarra.Java.digitacion;

public class AcordeLongitudImposibleException extends Exception {
	private String acorde;
	public AcordeLongitudImposibleException(String ac) {
		acorde = ac;
	}
	
	public String getAcordeRaro() {
		return acorde;
	}

}
