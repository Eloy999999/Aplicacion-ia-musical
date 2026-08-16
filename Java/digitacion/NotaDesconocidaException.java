package digitacion;

public class NotaDesconocidaException extends IllegalArgumentException {
	private String notaInalcanzable;
	public NotaDesconocidaException(String nota) {
		notaInalcanzable = nota;
	}
	
	public String getNotaInalcanzable() {
		return notaInalcanzable;
	}
}
