package com.digitarra.digitacion;

public class AcordeLongitudImposibleException extends Exception {
	private String acorde;
	public AcordeLongitudImposibleException(String ac) {
		super("El acorde \"" + ac + "\" tiene longitud no soportada");
		acorde = ac;
	}
	
	public String getAcordeRaro() {
		return acorde;
	}

}
