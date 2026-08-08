package digitacion;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import gestion_partituras.EmbajadorMusic21Python;
import gestion_partituras.Partitura;
import gestion_partituras.PartituraDigitada;


public class Digitador {
	
	private static final PosGuitarra[] cuerda_traste_do = {new PosGuitarra(2, 1), new PosGuitarra(5, 3),
															new PosGuitarra(3, 5), new PosGuitarra(1, 8),
															new PosGuitarra(6, 8), new PosGuitarra(4, 10)};
	
	private static final PosGuitarra[] cuerda_traste_re = {new PosGuitarra(4, 0), new PosGuitarra(2, 3),
															new PosGuitarra(5, 5), new PosGuitarra(3, 7),
															new PosGuitarra(1, 10), new PosGuitarra(6, 10),
															new PosGuitarra(4, 12)};
	
	private static final PosGuitarra[] cuerda_traste_mi = {new PosGuitarra(1, 0), new PosGuitarra(6, 0),
															new PosGuitarra(4, 2), new PosGuitarra(2, 5),
															new PosGuitarra(5, 7), new PosGuitarra(3, 9),
															new PosGuitarra(1, 12), new PosGuitarra(6, 12)};
	
	private static final PosGuitarra[] cuerda_traste_fa = {new PosGuitarra(1, 1), new PosGuitarra(6, 1),
															new PosGuitarra(4, 3), new PosGuitarra(2, 6),
															new PosGuitarra(5, 8), new PosGuitarra(3, 10)};
	
	private static final PosGuitarra[] cuerda_traste_sol = {new PosGuitarra(3, 0), new PosGuitarra(1, 3),
															new PosGuitarra(6, 3), new PosGuitarra(4, 5),
															new PosGuitarra(2, 8), new PosGuitarra(5, 10),
															new PosGuitarra(3, 12)};
	
	private static final PosGuitarra[] cuerda_traste_la = {new PosGuitarra(5, 0), new PosGuitarra(3, 2),
															new PosGuitarra(1, 5), new PosGuitarra(6, 5),
															new PosGuitarra(4, 7), new PosGuitarra(2, 10),
															new PosGuitarra(5, 12)};
	
	private static final PosGuitarra[] cuerda_traste_si = {new PosGuitarra(2, 0), new PosGuitarra(5, 2),
															new PosGuitarra(3, 4), new PosGuitarra(1, 7),
															new PosGuitarra(6, 7), new PosGuitarra(4, 9),
															new PosGuitarra(2, 12)};

	private static final int PENALIZACION_GRAVEDAD = 0;
	
	private static final int NUM_TRASTES = 13;
	
	private static final int NUM_CUERDAS = 6;
	
//	private JSONArray arrayNotasActual;
	
	public PartituraDigitada digita(Partitura part) {
		EmbajadorMusic21Python lector = new EmbajadorMusic21Python();
		
		JSONObject json_in = lector.getNotas(part.getPartitura_Midi().getRuta());
		
		JSONObject json_out = digitacion_cuerda_traste_iter(json_in.getJSONArray("notas"));
		
		String ruta_nuevo_archivo = lector.digitaPartitura(json_out);
		
		PartituraDigitada part_n = new PartituraDigitada(ruta_nuevo_archivo);
		
		return part_n;
	}
	
	private JSONObject digitacion_cuerda_traste_iter(JSONArray arrayNotas) {
//		JSONArray notas = infoNotas.getJSONArray("notas");
		 
//		for(int i = 0; i < arrayNotasActual.length(); i++) {
//			PosGuitarra[] candidatos = posiblesDigitaciones(notas.getString(i));
//			for(PosGuitarra c : candidatos) {
//				
//			}
//		}
		
		int n = arrayNotas.length();
		
		int[][][] matrizCoste = new int[n + 1][NUM_CUERDAS][NUM_TRASTES];
		
		
		//Inicializamos la matriz a INF
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < NUM_CUERDAS; j++) {
				for(int k = 0; k < NUM_TRASTES; k++) {
					matrizCoste[i][j][k] = Integer.MAX_VALUE;
				}
			}
		}
		
		//Inicializamos a 0 nota n ya que es cuando se ha terminado, ya que las notas van de [0,n)
		for(int j = 0; j < NUM_CUERDAS; j++) {
			for(int k = 0; k < NUM_TRASTES; k++) {
				matrizCoste[n][j][k] = 0;
			}
		}
		
		PosGuitarra[] mejorDigitacion = new PosGuitarra[n];
		
		
		//Rellenar la matriz menos la primera nota ya que no hay cuerda_traste anterior.
		for(int i = n - 1; i > 0; i--) {
			PosGuitarra[] posibles_nota_act = posiblesDigitaciones(arrayNotas.getString(i));
			PosGuitarra[] posibles_nota_ant = posiblesDigitaciones(arrayNotas.getString(i-1));
			for(int d_ant = 0; d_ant < posibles_nota_ant.length; d_ant++) {
				PosGuitarra pos_nota_ant = posibles_nota_ant[d_ant];
				
				for(int d_i = 0; d_i < posibles_nota_act.length; d_i++) {
					PosGuitarra pos_nota_act = posibles_nota_act[d_i];
					int costeAux = coste(pos_nota_ant, pos_nota_act) + matrizCoste[i+1][pos_nota_act.getCuerda()][pos_nota_act.getTraste()];
					if(costeAux < matrizCoste[i][pos_nota_ant.getCuerda()][pos_nota_ant.getTraste()]) {
						matrizCoste[i][pos_nota_ant.getCuerda()][pos_nota_ant.getTraste()] = costeAux;
					}
				}
			}
		}
		
		
		//Recolectar el mejor camino.
		
		for(int i = 0; i < arrayNotas.length(); i++) {
			PosGuitarra[] posibles_nota_i = posiblesDigitaciones(arrayNotas.getString(i));
			int mejorCoste = Integer.MAX_VALUE;
			PosGuitarra mejorDig_i = null;
			
			for(int j = 0; j < posibles_nota_i.length; j++) {
				PosGuitarra candidato_nota_i = posibles_nota_i[j];
				
				if(matrizCoste[i+1][candidato_nota_i.getCuerda()][candidato_nota_i.getTraste()] < mejorCoste) {
					mejorCoste = matrizCoste[i+1][candidato_nota_i.getCuerda()][candidato_nota_i.getTraste()];
					mejorDig_i = candidato_nota_i;
				}
			}
			mejorDigitacion[i] = mejorDig_i;
		}
		
		
		
		JSONObject json_digit = this.objetoDigitacionSalida(mejorDigitacion, matrizCoste[0][mejorDigitacion[0].getCuerda()][mejorDigitacion[0].getTraste()], n);
		
		return json_digit;
		
	}
	
	private int coste(PosGuitarra posini, PosGuitarra posfin) {
		int resul = 0;
		
		//Coste cambio cuerda
		
		if(posini.getCuerda() == 0 && posini.getTraste() == -1) { //Es la primera nota
			return 0;
		}
		else {
			//Calculo coste cuerda:
			int delta_cuerda = posfin.getCuerda() - posini.getCuerda();
			resul += Math.abs(delta_cuerda);
			
			//Calculo coste traste
			int delta_traste; 
			if(posfin.getTraste() == 0 || posini.getTraste() == 0) {
				delta_traste = 0;
			}
			else {
				delta_traste = Math.abs(posfin.getTraste() - posini.getTraste());
			}
			
			resul += delta_traste;
		}
		
		return resul;
	}
	
	
	private JSONObject objetoDigitacionSalida(PosGuitarra[] digitacionFinal, int mejorCoste, int numNotasYTrastes) {
		JSONObject json_salida = new JSONObject();
		
		JSONArray arrayDigitacion = new JSONArray();
		
		for(int i = 0; i < digitacionFinal.length; i++) {
			arrayDigitacion.put("%d,%d".formatted(digitacionFinal[i].getCuerda(), digitacionFinal[i].getTraste()));
		}
		
		json_salida.put("digitacion", arrayDigitacion);
		json_salida.put("coste", mejorCoste);
		json_salida.put("numNotasYTrastes", numNotasYTrastes);
		
		
		
		return json_salida;
	}
	
	private PosGuitarra[] posiblesDigitaciones(String nota) {
		if(nota.equalsIgnoreCase("do")) {
			return cuerda_traste_do;
		}
		else if(nota.equalsIgnoreCase("re")) {
			return cuerda_traste_re;
		}
		else if(nota.equalsIgnoreCase("mi")) {
			return cuerda_traste_mi;
		}
		else if(nota.equalsIgnoreCase("fa")) {
			return cuerda_traste_fa;
		}
		else if(nota.equalsIgnoreCase("sol")) {
			return cuerda_traste_sol;
		}
		else if(nota.equalsIgnoreCase("la")) {
			return cuerda_traste_la;
		}
		else if(nota.equalsIgnoreCase("si")) {
			return cuerda_traste_si;
		}
		else {
			System.out.println("Esto no es una nota");
			return null;
		}
	}
}
