import org.json.JSONObject;

import digitacion.Digitador;
import digitacion.NotaDesconocidaException;
import gestion_partituras.Partitura;

public class PruebasInterconexion {

	private static final String RUTA_ARCHIVO_PRUEBA = "/home/drm/Curso2526/TFG/Perfect_Ed_Sheeran.midi";
	
	
	public static void main(String[] args) {
		Partitura part = new Partitura("perfect", "", "RUTA_ARCHIVO_PRUEBA", RUTA_ARCHIVO_PRUEBA);
		Digitador digit = new Digitador();
		try {
//			JSONObject info = digit.digita(part);
//			System.out.print(info.toString());
			digit.digita(part);
		} catch(NotaDesconocidaException e) {
			System.out.println("No se pudo digitar, la nota " + e.getNotaInalcanzable() + "es desconocida o esta fuera de octava");
		}
	}
}
