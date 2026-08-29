package com.example.digitarra.Java;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.digitarra.Java.digitacion.AcordeLongitudImposibleException;
import com.example.digitarra.Java.digitacion.Digitador;
import com.example.digitarra.Java.digitacion.NotaDesconocidaException;
import com.example.digitarra.Java.gestion_partituras.Partitura;

public class PruebasInterconexion {

	private static final String RUTA_ARCHIVO_PRUEBA = "/home/drm/Curso2526/TFG/Perfect_Ed_Sheeran.midi";
	
	
	public static void main(String[] args) {
		Partitura part = new Partitura("perfect", "", "RUTA_ARCHIVO_PRUEBA", RUTA_ARCHIVO_PRUEBA, false);
		Digitador digit = new Digitador();
		try {
//			JSONObject info = digit.digita(part);
//			System.out.print(info.toString());
//			digit.digita(part);
			digit.digitaConAcordes(part);
		} catch(NotaDesconocidaException e) {
			System.out.println("No se pudo digitar, la nota " + e.getNotaInalcanzable() + "es desconocida o esta fuera de octava");
		} catch(AcordeLongitudImposibleException e1) {
			System.out.println("No se pudo digitar, El acorde " + e1.getAcordeRaro() + "tiene longitud imposible");
		} catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}
