package digitacion;

import org.json.JSONArray;
import org.json.JSONObject;

import gestion_partituras.EmbajadorMusic21Python;
import gestion_partituras.Partitura;
import gestion_partituras.PartituraDigitada;


public class Digitador {
	
	private class Dedo {
		private int dedo_der;
		private int dedo_izq;
		
		public Dedo(int d_i, int d_d) {
			dedo_der = d_d;
			dedo_izq = d_i;
		}
		

		public int getDedo_der() {
			return dedo_der;
		}

		public int getDedo_izq() {
			return dedo_izq;
		}
	}
	
	private static class PosGuitarra {
		private int cuerda;
		private int traste;
		
		public PosGuitarra(int cu, int tr) {
			cuerda = cu;
			traste = tr;
		}

		public int getCuerda() {
			return cuerda;
		}

		public int getTraste() {
			return traste;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
	            return true;
	        }

	        if (!(obj instanceof PosGuitarra)) {
	            return false;
	        }
	        
	        PosGuitarra p = (PosGuitarra) obj;
	        return this.cuerda == p.cuerda && this.traste == p.traste;
		}
		
		
	}
	
	private static class Configuracion {
		private PosGuitarra[] config_acorde;
		private PosGuitarra config_nota;
		
		public Configuracion(PosGuitarra confnota) {
			config_nota = confnota;
			config_acorde = null;
		}
		
		public Configuracion(PosGuitarra[] confacorde) {
			config_acorde = confacorde;
			config_nota = null;
		}
		
		public PosGuitarra getConfigNota() {
			return config_nota;
		}
		
		public PosGuitarra[] getConfigAcorde() {
			return config_acorde;
		}
		
		
	}
	
	private static class PosiblesConfiguraciones {
		private PosGuitarra[][] acordes;
		private PosGuitarra[] notas;
		private int numConfigs;
		
		public PosiblesConfiguraciones(PosGuitarra[] not) {
			acordes = null;
			notas = not;
			numConfigs = not.length;
		}
		
		public PosiblesConfiguraciones(PosGuitarra[][] acor) {
			notas = null;
			acordes = acor;
			numConfigs = acor.length;
		}
		
		public int getNumConfigs() {
			return numConfigs;
		}
		
		public PosGuitarra[] getPosibleAcorde(int i) {
			return acordes[i];
		}
		
		public PosGuitarra getPosibleNota(int i) {
			return notas[i];
		}
		
		
	}
	
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
	
	private static final PosGuitarra[] cuerda_traste_do5 = {new PosGuitarra(1, 8)};
	
	private static final PosGuitarra[] cuerda_traste_do4 = {new PosGuitarra(2, 1), new PosGuitarra(3, 5), new PosGuitarra(4, 10)};
	
	private static final PosGuitarra[] cuerda_traste_do3 = {new PosGuitarra(5, 3), new PosGuitarra(6, 8)};
	
	private static final PosGuitarra[] cuerda_traste_re5 = {new PosGuitarra(1, 10)};
	
	private static final PosGuitarra[] cuerda_traste_re4 = {new PosGuitarra(2, 3), new PosGuitarra(3, 7), new PosGuitarra(4, 12)};
	
	private static final PosGuitarra[] cuerda_traste_re3 = {new PosGuitarra(4, 0), new PosGuitarra(5, 5), new PosGuitarra(6, 10)};
	
	private static final PosGuitarra[] cuerda_traste_mi5 = {new PosGuitarra(1, 12)};
	
	private static final PosGuitarra[] cuerda_traste_mi4 = {new PosGuitarra(1, 0), new PosGuitarra(2, 5), new PosGuitarra(3, 9)};
	
	private static final PosGuitarra[] cuerda_traste_mi3 = {new PosGuitarra(4, 2), new PosGuitarra(5, 7), new PosGuitarra(6, 12)};
	
	private static final PosGuitarra[] cuerda_traste_mi2 = {new PosGuitarra(6, 0)};
	
	private static final PosGuitarra[] cuerda_traste_fa4 = {new PosGuitarra(1, 1), new PosGuitarra(2, 6), new PosGuitarra(3, 10)};

	private static final PosGuitarra[] cuerda_traste_fa3 = {new PosGuitarra(4, 3), new PosGuitarra(5, 8)};
	
	private static final PosGuitarra[] cuerda_traste_fa2 = {new PosGuitarra(6, 1)};
	
	private static final PosGuitarra[] cuerda_traste_sol4 = {new PosGuitarra(1, 3), new PosGuitarra(2, 8), new PosGuitarra(3, 12)};

	private static final PosGuitarra[] cuerda_traste_sol3 = {new PosGuitarra(3, 0), new PosGuitarra(4, 5), new PosGuitarra(5, 10)};
	
	private static final PosGuitarra[] cuerda_traste_sol2 = {new PosGuitarra(6, 3)};
	
	private static final PosGuitarra[] cuerda_traste_la4 = {new PosGuitarra(1, 5), new PosGuitarra(2, 10)};
	
	private static final PosGuitarra[] cuerda_traste_la3 = {new PosGuitarra(3, 2), new PosGuitarra(4, 7), new PosGuitarra(5, 12)};
	
	private static final PosGuitarra[] cuerda_traste_la2 = {new PosGuitarra(5, 0), new PosGuitarra(6, 5)};
	
	private static final PosGuitarra[] cuerda_traste_si4 = {new PosGuitarra(1, 7), new PosGuitarra(2, 12)};
	
	private static final PosGuitarra[] cuerda_traste_si3 = {new PosGuitarra(2, 0), new PosGuitarra(3, 4), new PosGuitarra(4, 9)};
	
	private static final PosGuitarra[] cuerda_traste_si2 = {new PosGuitarra(5, 2), new PosGuitarra(6, 7)};
	
	private static final int NUM_TRASTES = 13; //Número de trastes a considerar en la programación dinámica. 
	
	private static final int NUM_CUERDAS = 7; //Número de cuerdas a considerar en la programación dinámica.

	private static final int INICIO_FRANJA_ESTRECHA = 8; //Traste a partir del cual el espacio entre trastes es mas estrecho

	private static final double COSTE_FRANJA_ESTRECHA = 1.5; //Espacio(en cm) entre trastes en la franja estrecha

	private static final int INICIO_FRANJA_MEDIA = 5; //Traste a partir del cual el espacio entre trastes es medio, ni muy estrecho ni muy ancho

	private static final double COSTE_FRANJA_MEDIA = 2.0; //Espacio(en cm) entre trastes en la franja media

	private static final double COSTE_FRANJA_ANCHA = 3.0; //Espacio(en cm) entre trastes en la franja ancha
	
	private static final int ALTURA_INDICE_IZQUIERDO = 2; //Altura relativa del dedo índice de la mano izquierda al tocar la guitarra
	
	private static final int ALTURA_CORAZON_IZQUIERDO = 3; //Altura relativa del dedo corazón de la mano izquierda al tocar la guitarra
	
	private static final int ALTURA_ANULAR_IZQUIERDO = 2; //Altura relativa del dedo anular de la mano izquierda al tocar la guitarra
	
	private static final int ALTURA_MEÑIQUE_IZQUIERDO = 1; //Altura relativa del dedo meñique de la mano izquierda al tocar la guitarra
	
	private static final int MEÑIQUE_IZQUIERDO = 3; //Identificador del dedo meñique de la mano izquierda para la programación dinámica
	
	private static final int ANULAR_IZQUIERDO = 2; //Identificador del dedo anular de la mano izquierda para la programación dinámica
	
	private static final int CORAZON_IZQUIERDO = 1; //Identificador del dedo corazon de la mano izquierda para la programación dinámica
	
	private static final int INDICE_IZQUIERDO = 0; //Identificador del dedo indice de la mano izquierda para la programación dinámica

	private static final double PENALIZACION_MEÑIQUE_ALTO = 1.5; //Factor de penalización aplicado al coste al tener que subir demasiado el meñique
	
	private static final int ALTURA_PULGAR_DERECHO = 5; //Altura relativa del dedo pulgar de la mano derecha al tocar la guitarra
	
	private static final int ALTURA_INDICE_DERECHO = 4; //Altura relativa del dedo índice de la mano derecha al tocar la guitarra
	
	private static final int ALTURA_CORAZON_DERECHO = 3; //Altura relativa del dedo corazón de la mano derecha al tocar la guitarra
	
	private static final int ALTURA_ANULAR_DERECHO = 2; //Altura relativa del dedo anular de la mano derecha al tocar la guitarra
	
	private static final int ALTURA_MEÑIQUE_DERECHO = 1; //Altura relativa del dedo meñique de la mano derecha al tocar la guitarra
	
	private static final int PULGAR_DERECHO = 4; //Identificador del dedo pulgar de la mano derecha para la programación dinámica
	
	private static final int INDICE_DERECHO = 3; //Identificador del dedo indice de la mano derecha para la programación dinámica
	
	private static final int CORAZON_DERECHO = 2; //Identificador del dedo corazon de la mano derecha para la programación dinámica
	
	private static final int ANULAR_DERECHO = 1; //Identificador del dedo anular de la mano derecha para la programación dinámica
	
	private static final int MEÑIQUE_DERECHO = 0; //Identificador del dedo meñique de la mano derecha para la programación dinámica

	private static final char CARACTER_PULGAR = 'p'; //Carácter del dedo pulgar para anotar directamente en la partitura en pdf

	private static final char CARACTER_INDICE = 'i'; //Carácter del dedo indice para anotar directamente en la partitura en pdf

	private static final char CARACTER_CORAZON = 'c'; //Carácter del dedo corazon para anotar directamente en la partitura en pdf

	private static final char CARACTER_ANULAR = 'a'; //Carácter del dedo anular para anotar directamente en la partitura en pdf

	private static final char CARACTER_MEÑIQUE = 'm'; //Carácter del dedo meñique para anotar directamente en la partitura en pdf

	private static final char CARACTER_DEDO_DESCONOCIDO = '?'; //Carácter para cuando no se reconoce el dedo de la digitacion.

	private static final int MAX_CONFIGURACIONES = 81;
	
	
	/**
	 * Funcion que genera la mejor digitacion posible para una partitura dada, usando programación dinámica.
	 * 
	 * Una vez la calcula, llama al embajador para que haga las anotaciones en el archivo MusicXML 
	 * y devuelva la ruta del nuevo archivo MusicXML con la digitacion. Tras ello crea un nuevo objeto PartituraDigitada y lo devuelve.
	 * 
	 * @param partitura Partitura de guitarra que no ha sido digitada.
	 * @return instancia de {@link PartituraDigitada} con la partitura digitada.
	 */
	public PartituraDigitada digita(Partitura partitura) throws NotaDesconocidaException {
		EmbajadorMusic21Python embajador = new EmbajadorMusic21Python();
		String ruta_xml = "";
		JSONObject json_in = embajador.getNotas(partitura.getPartitura_Midi().getRuta());
		System.out.println("Se leyeron bien las notas");
//		JSONObject json_out = digitacion_cuerda_traste_iter(json_in.getJSONArray("notas"));
		
		PosGuitarra[] cuerda_traste_mejor = digitacion_cuerda_traste_iter(json_in.getJSONArray("notas"));
		
		Dedo[] dedos_mejor = digitacion_dedos_iter(json_in.getJSONArray("notas"), cuerda_traste_mejor);
		System.out.println("Se hizo la digitacion");
		JSONObject infor_digit = this.objetoDigitacionSalida(cuerda_traste_mejor, dedos_mejor, 33, dedos_mejor.length, json_in.getJSONArray("notas"));
		infor_digit.put("archivo_out", "/home/drm/Curso2526/TFG/PartiturasDigitadas/"+partitura.getNombre_partitura()+"digitado.xml");
		infor_digit.put("archivo_in", partitura.getPartitura_Midi().getRuta());
		embajador.digitaPartitura(infor_digit);
		
		//TODO:Pedirle al embajador que cree el pdf de la parittura
		String rutaPdfDigitada = "";
		
		
		PartituraDigitada resul = new PartituraDigitada(partitura.getNombre_partitura()+"_digitada", rutaPdfDigitada, ruta_xml);
		
		return resul;
	}
	
	
	
	/*
	 * 
	 * FUNCIONES PARA DIGITACION CUERDA-TRASTE
	 */
	
	private Configuracion[] digitacion_cuerda_traste_acordes_iter(JSONArray arrayNotas) {
		int n = arrayNotas.length();
		PosiblesConfiguraciones[] configs = new PosiblesConfiguraciones[n];
		
		boolean[] esAcorde = new boolean[n];
		for(int i = 0; i < n; i++) {
			String perei = arrayNotas.getString(i);
			if(esNota(perei)) {
				esAcorde[i] = false;
				configs[i] = new PosiblesConfiguraciones(posiblesDigitaciones(perei));
			}
			else {
				esAcorde[i] = true;
				configs[i] = new PosiblesConfiguraciones(posiblesDigitacionesAcorde(perei));
			}
		}
		int[][] matrizCoste = new int[n + 1][MAX_CONFIGURACIONES];
		
		
		//Inicializamos la matriz a INF
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < MAX_CONFIGURACIONES; j++) {
				matrizCoste[i][j] = Integer.MAX_VALUE;
			}
		}
		
		//Inicializamos a 0 nota n ya que es cuando se ha terminado, ya que las notas van de [0,n)
		for(int j = 0; j < MAX_CONFIGURACIONES; j++) {
				matrizCoste[n][j] = 0;
		}
		
		//Rellenar la matriz menos la primera nota/acorde ya que no hay cuerda_traste anterior.
		for(int i = n - 1; i > 0; i--) {
			PosiblesConfiguraciones posibles_elemento_act = configs[i];
			PosiblesConfiguraciones posibles_elemento_ant = configs[i-1];
			for(int j_ant = 0; j_ant < posibles_elemento_ant.getNumConfigs(); j_ant++) {
				for(int j_act = 0; j_act < posibles_elemento_act.getNumConfigs(); j_act++) {
//					PosGuitarra pos_nota_act = posibles_nota_act[d_i];
//					int costeAux = coste(pos_nota_ant, pos_nota_act) + matrizCoste[i+1][pos_nota_act.getCuerda()][pos_nota_act.getTraste()];
//					if(costeAux < matrizCoste[i][pos_nota_ant.getCuerda()][pos_nota_ant.getTraste()]) {
//						matrizCoste[i][pos_nota_ant.getCuerda()][pos_nota_ant.getTraste()] = costeAux;
//					}
					int costeAux = matrizCoste[i+1][j_act];
					if(esAcorde[i-1] && esAcorde[i]) { //Ambos son acordes
						costeAux += costeAcordeAcorde(posibles_elemento_ant.getPosibleAcorde(j_ant), posibles_elemento_act.getPosibleAcorde(j_act));
					}
					else if(esAcorde[i-1] && !esAcorde[i]) { //Anterior es acorde y actual es nota
						costeAux += costeAcordeNota(posibles_elemento_ant.getPosibleAcorde(j_ant), posibles_elemento_act.getPosibleNota(j_act));
					}
					else if(!esAcorde[i-1] && esAcorde[i]) { //Anterior es nota y actual es acorde
						costeAux += costeNotaAcorde(posibles_elemento_ant.getPosibleNota(j_ant), posibles_elemento_act.getPosibleAcorde(j_act));
					}
					else { //Ambos son notas
						costeAux += coste(posibles_elemento_ant.getPosibleNota(j_ant), posibles_elemento_act.getPosibleNota(j_act));
					}
					matrizCoste[i][j_ant] = Math.min(costeAux, matrizCoste[i][j_ant]);
				}
			}
		}
		
		//Recolectar el mejor camino.
		
//		PosGuitarra[] mejorDigitacion = new PosGuitarra[n];
		
		Configuracion[] mejorDigitacion = new Configuracion[n];
		
		for(int i = 0; i < arrayNotas.length() - 1; i++) {
//			PosGuitarra[] posibles_nota_i = posiblesDigitaciones(arrayNotas.getString(i));
//			int mejorCoste = Integer.MAX_VALUE;
//			PosGuitarra mejorDig_i = null;
			
			PosiblesConfiguraciones posibles_nota_i = configs[i];
			int mejorCoste = Integer.MAX_VALUE;
			int indexMejor = -1;
			
			for(int j = 0; j < posibles_nota_i.getNumConfigs(); j++) {
//				PosGuitarra candidato_nota_i = posibles_nota_i[j];
				
//				if(matrizCoste[i+1][candidato_nota_i.getCuerda()][candidato_nota_i.getTraste()] < mejorCoste) {
//					mejorCoste = matrizCoste[i+1][candidato_nota_i.getCuerda()][candidato_nota_i.getTraste()];
//					mejorDig_i = candidato_nota_i;
//				}
				if(matrizCoste[i+1][j] < mejorCoste) {
					mejorCoste = matrizCoste[i+1][j];
					indexMejor = j;
				}
			}
			if(esAcorde[i]) {
				mejorDigitacion[i] = new Configuracion(posibles_nota_i.getPosibleAcorde(indexMejor));
			}
			else {
				mejorDigitacion[i] = new Configuracion(posibles_nota_i.getPosibleNota(indexMejor));
			}
			
		}
		
//		PosGuitarra[] posibles_ultima = posiblesDigitaciones(arrayNotas.getString(n-1));
		PosiblesConfiguraciones posibles_ultima = configs[n-1];
		int mejorAux = Integer.MAX_VALUE;
		for(int j = 0; j < posibles_ultima.getNumConfigs(); j++) {
			
//			int aux = coste(mejorDigitacion[n-2], posibles_ultima[i]);
			int aux = 0;
			Configuracion posos = null;
			if(esAcorde[n-2] && esAcorde[n-1]) { //Ambos son acordes
				aux = costeAcordeAcorde(mejorDigitacion[n-2].getConfigAcorde(), posibles_ultima.getPosibleAcorde(j));
				posos = new Configuracion(posibles_ultima.getPosibleAcorde(j));
			}
			else if(esAcorde[n-2] && !esAcorde[n-1]) { //Anterior es acorde y actual es nota
				aux = costeAcordeNota(mejorDigitacion[n-2].getConfigAcorde(), posibles_ultima.getPosibleNota(j));
				posos = new Configuracion(posibles_ultima.getPosibleNota(j));
			}
			else if(!esAcorde[n-2] && esAcorde[n-1]) { //Anterior es nota y actual es acorde
				aux = costeNotaAcorde(mejorDigitacion[n-2].getConfigNota(), posibles_ultima.getPosibleAcorde(j));
				posos = new Configuracion(posibles_ultima.getPosibleAcorde(j));
			}
			else { //Ambos son notas
				aux = coste(mejorDigitacion[n-2].getConfigNota(), posibles_ultima.getPosibleNota(j));
				posos = new Configuracion(posibles_ultima.getPosibleNota(j));
			}
			if(aux < mejorAux) {
				mejorAux = aux;
				mejorDigitacion[n-1] = posos;
			}
		}
		
		return mejorDigitacion;
	}
	
	private int costeAcordeAcorde(PosGuitarra[] posibleAcordeAnt, PosGuitarra[] posibleAcordeSig) {
		int maximoCoste = Integer.MIN_VALUE;
		
		for(int i = 0; i < posibleAcordeAnt.length; i++) {
			for(int j = 0; j < posibleAcordeSig.length; j++) {
				maximoCoste = Math.max(maximoCoste, coste(posibleAcordeAnt[i], posibleAcordeSig[j]));
			}
		}
		
		return maximoCoste;
	}

	private int costeAcordeNota(PosGuitarra[] posibleAcordeAnt, PosGuitarra posibleNotaSig) {
		int maximoCoste = Integer.MIN_VALUE;
		
		for(int i = 0; i < posibleAcordeAnt.length; i++) {
			maximoCoste = Math.max(maximoCoste, coste(posibleAcordeAnt[i], posibleNotaSig));
		}
		
		return maximoCoste;
	}
	
	private int costeNotaAcorde(PosGuitarra posibleNotaAnt, PosGuitarra[] posibleAcordeSig) {
		int maximoCoste = Integer.MIN_VALUE;
		
		for(int j = 0; j < posibleAcordeSig.length; j++) {
			maximoCoste = Math.max(maximoCoste, coste(posibleNotaAnt, posibleAcordeSig[j]));
		}
		
		return maximoCoste;
	}

	private PosGuitarra[][] posiblesDigitacionesAcorde(String perei) {
		String[] notasEnAcorde = perei.split(",");
		
		PosGuitarra[][] resul = null;
		PosGuitarra[] posiblesNota1 = null;
		PosGuitarra[] posiblesNota2 = null;
		PosGuitarra[] posiblesNota3 = null;
		PosGuitarra[] posiblesNota4 = null;
		int numPosiblesDigitaciones = 0;
		int cont = 0;
		
		switch(notasEnAcorde.length) {
		case 2:
			posiblesNota1 = posiblesDigitaciones(notasEnAcorde[0]);
			posiblesNota2 = posiblesDigitaciones(notasEnAcorde[1]);
			numPosiblesDigitaciones = posiblesNota1.length * posiblesNota2.length;
			resul = new PosGuitarra[numPosiblesDigitaciones][2];
			for(int i = 0; i < posiblesNota1.length; i++) {
				PosGuitarra pos_i = posiblesNota1[i];
				for(int j = 0; j < posiblesNota2.length; j++) {
					resul[cont][0] = pos_i;
					resul[cont][1] = posiblesNota2[j];
					cont++;
				}
			}
			break;
		case 3:
			posiblesNota1 = posiblesDigitaciones(notasEnAcorde[0]);
			posiblesNota2 = posiblesDigitaciones(notasEnAcorde[1]);
			posiblesNota3 = posiblesDigitaciones(notasEnAcorde[2]);
			numPosiblesDigitaciones = posiblesNota1.length * posiblesNota2.length * posiblesNota3.length;
			resul = new PosGuitarra[numPosiblesDigitaciones][3];
			for(int i = 0; i < posiblesNota1.length; i++) {
				PosGuitarra pos_i = posiblesNota1[i];
				for(int j = 0; j < posiblesNota2.length; j++) {
					PosGuitarra pos_j = posiblesNota2[j];
					for(int k = 0; k < posiblesNota3.length; k++) {
						resul[cont][0] = pos_i;
						resul[cont][1] = pos_j;
						resul[cont][2] = posiblesNota3[k];
						cont++;
					}
				}
			}
			break;
		case 4:
			posiblesNota1 = posiblesDigitaciones(notasEnAcorde[0]);
			posiblesNota2 = posiblesDigitaciones(notasEnAcorde[1]);
			posiblesNota3 = posiblesDigitaciones(notasEnAcorde[2]);
			posiblesNota4 = posiblesDigitaciones(notasEnAcorde[3]);
			numPosiblesDigitaciones = posiblesNota1.length * posiblesNota2.length * posiblesNota3.length * posiblesNota4.length;
			resul = new PosGuitarra[numPosiblesDigitaciones][4];
			for(int i = 0; i < posiblesNota1.length; i++) {
				PosGuitarra pos_i = posiblesNota1[i];
				for(int j = 0; j < posiblesNota2.length; j++) {
					PosGuitarra pos_j = posiblesNota2[j];
					for(int k = 0; k < posiblesNota3.length; k++) {
						PosGuitarra pos_k = posiblesNota3[k];
						for(int l = 0; l < posiblesNota4.length; l++) {
							resul[cont][0] = pos_i;
							resul[cont][1] = pos_j;
							resul[cont][2] = pos_k;
							resul[cont][3] = posiblesNota4[l];
							cont++;
						}
					}
				}
			}
			break;
		default:
			throw new AcordeLongitudImposibleException(perei);
		}
		
		return resul;
	}



	private boolean esNota(String notaOAcorde) {
		return !notaOAcorde.contains(",");
	}



	/**
	 * Función auxiliar que genera la mejor digitación de cuerda y traste para tocar cada nota de una partitura, usando programación dinámica de 
	 * forma iterativa, rellenando la matriz
	 * hacia atrás.
	 * @param arrayNotas {@link JSONArray} que contiene las notas de la partitura a digitar
	 * @return Array de Cuerda y traste con los que tocar cada nota haciendo el coste mínimo.
	 */
	private PosGuitarra[] digitacion_cuerda_traste_iter(JSONArray arrayNotas) {
		
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
		
		PosGuitarra[] mejorDigitacion = new PosGuitarra[n];
		
		for(int i = 0; i < arrayNotas.length() - 1; i++) {
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
		
		PosGuitarra[] posibles_ultima = posiblesDigitaciones(arrayNotas.getString(n-1));
		int mejorAux = Integer.MAX_VALUE;
		for(int i = 0; i < posibles_ultima.length; i++) {
			
			int aux = coste(mejorDigitacion[n-2], posibles_ultima[i]);
			if(aux < mejorAux) {
				mejorAux = aux;
				mejorDigitacion[n-1] = posibles_ultima[i];
			}
		}
		
		
		
		
		return mejorDigitacion;
		
	}
	
	
	/**
	 * Función auxiliar que calcula el coste de pasar de una posición(Cuerda y traste) a otra en una guitarra, dado:
	 * @param posini Posición en la guitarra de la que se parte
	 * @param posfin Posición en la guitarra a la que se pretende mover la mano.
	 * @return Coste de moverse de una posicion a otra en la guitarra
	 */
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
	
	/**
	 * Función auxiliar que dado un string de una nota, devuelve las posibles Posiciones(Cuerda y traste) en las que se puede tocar dicha nota,
	 * @param nota String con el nombre de la nota
	 * @return Posibles Posiciones donde tocar la nota dada.
	 */
	private PosGuitarra[] posiblesDigitaciones(String nota) throws NotaDesconocidaException{
		
		
//		if(nota.equalsIgnoreCase("do")) {
//			return cuerda_traste_do;
//		}
//		else if(nota.equalsIgnoreCase("re")) {
//			return cuerda_traste_re;
//		}
//		else if(nota.equalsIgnoreCase("mi")) {
//			return cuerda_traste_mi;
//		}
//		else if(nota.equalsIgnoreCase("fa")) {
//			return cuerda_traste_fa;
//		}
//		else if(nota.equalsIgnoreCase("sol")) {
//			return cuerda_traste_sol;
//		}
//		else if(nota.equalsIgnoreCase("la")) {
//			return cuerda_traste_la;
//		}
//		else if(nota.equalsIgnoreCase("si")) {
//			return cuerda_traste_si;
//		}
//		else {
//			System.out.println("Esto no es una nota");
//			return null;
//		}
		
		
		String notaAux = nota;
		String[] notas = nota.split(",");
		if(notas.length > 0) {
			notaAux = notas[0];
		}
		boolean esBemol = notaAux.contains("-");
		boolean esSostenido = notaAux.contains("#");
		notaAux = notaAux.replace("-", "");
		notaAux = notaAux.replace("#", "");
		
		

		PosGuitarra[] resul;
		
		switch(notaAux) {
		case "do5":
			resul = cuerda_traste_do5;
			break;
		case "do4":
			resul = cuerda_traste_do4;
			break;
		case "do3":
			resul = cuerda_traste_do3;
			break;
		case "re5":
			resul = cuerda_traste_re5;
			break;
		case "re4":
			resul = cuerda_traste_re4;
			break;
		case "re3":
			resul = cuerda_traste_re3;
			break;
		case "mi5":
			resul = cuerda_traste_mi5;
			break;
		case "mi4":
			resul = cuerda_traste_mi4;
			break;
		case "mi3":
			resul = cuerda_traste_mi3;
			break;
		case "mi2":
			resul = cuerda_traste_mi2;
			break;
		case "fa4":
			resul = cuerda_traste_fa4;
			break;
		case "fa3":
			resul = cuerda_traste_fa3;
			break;
		case "fa2":
			resul = cuerda_traste_fa2;
			break;
		case "sol4":
			resul = cuerda_traste_sol4;
			break;
		case "sol3":
			resul = cuerda_traste_sol3;
			break;
		case "sol2":
			resul = cuerda_traste_sol2;
			break;
		case "la4":
			resul = cuerda_traste_la4;
			break;
		case "la3":
			resul = cuerda_traste_la3;
			break;
		case "la2":
			resul = cuerda_traste_la2;
			break;
		case "si4":
			resul = cuerda_traste_si4;
			break;
		case "si3":
			resul = cuerda_traste_si3;
			break;
		case "si2":
			resul = cuerda_traste_si2;
			break;
		default:
			throw new NotaDesconocidaException(notaAux);
		}
		
		if(esBemol) {
			resul = resul.clone();
			for(int i = 0; i < resul.length; i++) {
//				resul[i].setTraste(resul[i].getTraste()-1);
				resul[i] = new PosGuitarra(resul[i].getCuerda(), resul[i].getTraste()-1);
			}
		}
		else if(esSostenido) {
			resul = resul.clone();
			for(int i = 0; i < resul.length; i++) {
//				resul[i].setTraste(resul[i].getTraste()+1);
				resul[i] = new PosGuitarra(resul[i].getCuerda(), Math.min(NUM_TRASTES-1,resul[i].getTraste()+1));
			}
		}
		
		return resul;
	}
	
	/*
	 * 
	 * FUNCIONES PARA LA DIGITACION DE LOS DEDOS
	 */
	
	/**
	 * Funcion que se encarga de calcular la mejor digitación de los dedos de ambas manos para cada nota de una partitura. Usando programación dinámica 
	 * de manera iterativa, rellenando la matriz de coste hacia atras.
	 * @param notas {@link JSONArray} que contiene las notas de la partitura a digitar.
	 * @param cuerda_traste_mejor array de {@link PosGuitarra} que contiene las cuerdas y trastes elegidos anteriormente para tocar cada nota de la 
	 * partitura
	 * @return array de {@link Dedo} que contiene los dedos de cada mano elegidos para tocar cada nota de la partitura
	 */
	private Dedo[] digitacion_dedos_iter(JSONArray notas, PosGuitarra[] cuerda_traste_mejor) {
		int n = notas.length();
		double[][] costeManoIzquierda = new double[n + 1][4];
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < 4; j++) {
				costeManoIzquierda[i][j] = Double.MAX_VALUE;
			}
		}
		
		
		for(int j = 0; j < 4; j++) {
			costeManoIzquierda[n][j] = 0.0;
		}
		
		
		//Rellenamos matriz
		for(int i = n - 1; i > 0; i--) {
			for(int d = 0; d < 4; d++) {
				for(int d_i = 0; d_i < 4; d_i++) {
					double costeAux = costeDesplManoIzq(d, d_i, cuerda_traste_mejor[i-1], cuerda_traste_mejor[i]) + costeManoIzquierda[i+1][d_i];
					costeManoIzquierda[i][d] = Math.min(costeManoIzquierda[i][d], costeAux);
				}
			}
		}
		
		//Recuperamos mejor camino para la mano izquierda
		int[] mejorCaminoIzq = new int[n];
		for(int i = 0; i < n - 1; i++) {
			double mejorCoste = Double.MAX_VALUE;
			int dedo_mejor = -1;
			for(int d = 0; d < 4; d++) {
				if(costeManoIzquierda[i+1][d] < mejorCoste) {
					dedo_mejor = d;
					mejorCoste = costeManoIzquierda[i+1][d];
				}
			}
			
			mejorCaminoIzq[i] = dedo_mejor;
		}
		
		double mejorCoste = Double.MAX_VALUE;
		int dedo_mejor = -1;
		for(int d = 0; d < 4; d++) {
			double aux = costeDesplManoIzq(mejorCaminoIzq[n-2], d, cuerda_traste_mejor[n-2], cuerda_traste_mejor[n-1]);
			if(aux < mejorCoste) {
				dedo_mejor = d;
				mejorCoste = aux;
			}
		}
		
		mejorCaminoIzq[n-1] = dedo_mejor;
		
		/*
		 * 
		 * DIGITACION MANO DERECHA
		 * 
		 */
		
		int[][] costeManoDerecha = new int[n+1][5];
		
		for(int i = 0; i < n; i++) {
			for(int j = 0; j < 5; j++) {
				costeManoDerecha[i][j] = Integer.MAX_VALUE;
			}
		}

		for(int j = 0; j < 5; j++) {
			costeManoDerecha[n][j] = 0;
		}
		
		for(int i = n - 1; i > 0; i--) {
			for(int d = 0; d < 5; d++) {
				for(int d_i = 0; d_i < 5; d_i++) {
					int costeAux = costeDesplManoDer(d, d_i, cuerda_traste_mejor[i-1], cuerda_traste_mejor[i]) + costeManoDerecha[i+1][d_i];
					costeManoDerecha[i][d] = Math.min(costeManoDerecha[i][d], costeAux);
				}
			}
		}
		
		//Recolectamos mejor camino mano derecha
		int[] mejorCaminoDer = new int[n];
		for(int i = 0; i < n - 1; i++) {
			int mejorAct = Integer.MAX_VALUE;
			for(int d = 0; d < 5; d++) {
				if(costeManoDerecha[i+1][d] < mejorAct) {
					mejorAct = costeManoDerecha[i+1][d];
					mejorCaminoDer[i] = d;
				}
			}
		}
		
		int mejorAct = Integer.MAX_VALUE;
		for(int d = 0; d < 5; d++) {
			int aux = costeDesplManoDer(mejorCaminoDer[n-2], d, cuerda_traste_mejor[n-2], cuerda_traste_mejor[n-1]);
			if(aux < mejorAct) {
				mejorAct = aux;
				mejorCaminoDer[n-1] = d;
			}
		}
		
		//Guardamos ambos resultados
		
		Dedo[] resul = new Dedo[n];
		for(int i = 0; i < n; i++) {
			resul[i] = new Dedo(mejorCaminoIzq[i] + 1, mejorCaminoDer[i]);
		}
		
		return resul;
	}

	/*
	 * FUNCIONES AUXILIARES PARA LA MANO DERECHA
	 */
	
	/**
	 * Función auxiliar que sirve para calcular el coste de mover la mano DERECHA para tocar la siguiente nota dados:
	 * @param dedo_act dedo que tocó la ultima nota
	 * @param dedo_sig dedo que pretende tocar la siguiente nota
	 * @param posGuitarraAct cuerda y traste en la que se toco la ultima nota
	 * @param posGuitarraSig cuerda y traste en la que se tocará la siguiente nota
	 * @return Coste de mover la mano derecha con los parámetros proporcionados
	 */
	private int costeDesplManoDer(int dedo_act, int dedo_sig, PosGuitarra posGuitarraAct, PosGuitarra posGuitarraSig) {
		
		if(posGuitarraSig.getCuerda() == -1 || posGuitarraAct.getCuerda() == -1) {
			return 0;
		}
		int cuerdaActualDedoSig = calculo_cuerda_dedo_der(dedo_act, dedo_sig, posGuitarraAct);
		return Math.abs(cuerdaActualDedoSig - posGuitarraSig.getCuerda());
	}

	/**
	 * Función auxiliar que calcula la cuerda sobre la que se encuentra un dedo segun la posición de la mano. No significa que este tocando esa cuerda,
	 * pero calcula como de lejos(en términos de cuerdas) que esta un dedo de otro segun la ultima nota tocada.
	 * @param dedo_act Dedo que tóco la ultima nota
	 * @param dedo_sig Dedo que pretende tocar la siguiente nota
	 * @param posGuitarraAct Cuerda y traste donde se tocó la última nota.
	 * @return Cuerda en la que se encuentra el dedo
	 */
	private int calculo_cuerda_dedo_der(int dedo_act, int dedo_sig, PosGuitarra posGuitarraAct) {
		int diffAlturaDedos = altura_der(dedo_act) - altura_der(dedo_sig);
		
		return Math.max(-1, posGuitarraAct.getCuerda() - diffAlturaDedos);
	}

	/**
	 * Función auxiliar que calcula la altura relativa de un dedo de la mano derecha al tocar la guitarra
	 * @param dedo_act dedo del que se quiere saber su altura
	 * @return altura del dedo dado
	 */
	private int altura_der(int dedo_act) {
		switch(dedo_act) {
		case PULGAR_DERECHO:
			return ALTURA_PULGAR_DERECHO;
		case INDICE_DERECHO:
			return ALTURA_INDICE_DERECHO;
		case CORAZON_DERECHO:
			return ALTURA_CORAZON_DERECHO;
		case ANULAR_DERECHO:
			return ALTURA_ANULAR_DERECHO;
		case MEÑIQUE_DERECHO:
			return ALTURA_MEÑIQUE_DERECHO;
		default:
			return -1;
		}
	}
	
	
	/*
	 * 
	 * FUNCIONES AUXILIARES PARA LA MANO IZQUIERDA
	 */

	/**
	 * Funcion auxiliar que calcula el coste de desplazar la mano izquierda al tocar la guitarra pasando de una nota a otra.
	 * @param dedo_actual dedo con el que se tocó la última nota
	 * @param dedo_siguiente_candidato dedo con el que se pretende tocar la siguiente nota
	 * @param posGuitarraAct Cuerda y traste en los que se tocó la última nota.
	 * @param posGuitarraSig Cuerda y traste en los que se va a tocar la siguiente nota.
	 * @return coste de Mover la mano izquierda
	 */
	private double costeDesplManoIzq(int dedo_actual, int dedo_siguiente_candidato, PosGuitarra posGuitarraAct, PosGuitarra posGuitarraSig) {
		PosGuitarra pos_actual_dedo_candidato = calculo_pos_dedo(dedo_actual, dedo_siguiente_candidato, posGuitarraAct);
		double resul;
		
		if(pos_actual_dedo_candidato.equals(posGuitarraSig) || posGuitarraAct.getTraste() == 0) {
			resul = 0.0;
		}
		else {
			resul = desp_h(pos_actual_dedo_candidato, posGuitarraSig) + desp_v(pos_actual_dedo_candidato, posGuitarraSig, dedo_siguiente_candidato);
		}
		
		return resul;
	}

	/**
	 * Funcion auxiliar que calcula el coste del desplazamiento HORIZONTAL de la mano izquierda y, más concretamente, del dedo que se pretende usar dado:
	 * @param pos_actual_dedo_candidato cuerda y traste en los que se encuentra el dedo que se va a usar
	 * @param posGuitarraSig Cuerda y traste en los que se tiene que colocar el dedo que se va a usar
	 * @return Coste del desplazamiento horizontal de la mano izquierda.
	 */
	private double desp_h(PosGuitarra pos_actual_dedo_candidato, PosGuitarra posGuitarraSig) {
		int t_act = pos_actual_dedo_candidato.getTraste();
		int t_sig = posGuitarraSig.getTraste();
		double coste = 0.0;
		if(t_act == t_sig) {
			return 0.0;
		}
		else {
			int maxi = Math.max(t_act, t_sig);
			int mini = Math.min(t_act, t_sig);
			while(maxi != mini) {
				if(maxi >= INICIO_FRANJA_ESTRECHA) {
					coste += COSTE_FRANJA_ESTRECHA;
				}
				else if(maxi >= INICIO_FRANJA_MEDIA) {
					coste += COSTE_FRANJA_MEDIA;
				}
				else {
					coste += COSTE_FRANJA_ANCHA;
				}
				maxi--;
			}
		}
		
		return coste;
	}

	/**
	 * Función auxiliar que calcula el coste del desplazamiento VERTICAL de la mano izquierda, dado:
	 * @param pos_actual_dedo_candidato Cuerda y traste en los que se encuentra el dedo que se va a usar
	 * @param posGuitarraSig Cuerda y traste a los que se tiene que mover el dedo
	 * @param dedoSig Dedo que pretende tocar la nota
	 * @return Coste del desplazamiento vertical de la mano izquierda.
	 */
	private double desp_v(PosGuitarra pos_actual_dedo_candidato, PosGuitarra posGuitarraSig, int dedoSig) {
		
		double coste = Math.abs(posGuitarraSig.getCuerda() - pos_actual_dedo_candidato.getCuerda());
		
		if(posGuitarraSig.getCuerda() >= 4 && dedoSig == MEÑIQUE_IZQUIERDO) { // penalizacion por llevar el meñique tan arriba
			coste *= PENALIZACION_MEÑIQUE_ALTO;
		}
		
		return coste;
	}

	/**
	 * Función auxiliar que calcula la posición(Cuerda y traste) en la que se encuentra EN LA MANO DERECHA un dedo según donde se encuentra otro dedo
	 * @param dedo_actual Dedo del cual se conoce la posición
	 * @param dedo_siguiente_candidato Dedo del cual se pretende conocer su posición
	 * @param posGuitarraAct Posición del dedo {@link dedo_actual}
	 * @return Posición en la que se encuentra el dedo {@link dedo_siguiente_candidato} 
	 */
	private PosGuitarra calculo_pos_dedo(int dedo_actual, int dedo_siguiente_candidato, PosGuitarra posGuitarraAct) {
		
		if(dedo_actual == dedo_siguiente_candidato) {
			return posGuitarraAct;
		}
		else {
			//Calculo traste: se asume que los 4 dedos ocupan 4 trastes contiguos
			int traste = posGuitarraAct.getTraste() - (dedo_actual - dedo_siguiente_candidato);
			int cuerda = posGuitarraAct.getCuerda();
			//Calcula cuerda
			cuerda = cuerda - (altura_izq(dedo_actual) - altura_izq(dedo_siguiente_candidato));
			
			return new PosGuitarra(cuerda, traste);
		}
		
	}
	/**
	 * Funcion auxiliar que calcula la altura relativa de un dedo de la mano izquierda
	 * @param dedo dedo del cual se pretende conocer su altura
	 * @return altura relativa del dedo
	 */
	private int altura_izq(int dedo) {
		switch(dedo) {
		case INDICE_IZQUIERDO:
			return ALTURA_INDICE_IZQUIERDO;
		case CORAZON_IZQUIERDO:
			return ALTURA_CORAZON_IZQUIERDO;
		case ANULAR_IZQUIERDO:
			return ALTURA_ANULAR_IZQUIERDO;
		case MEÑIQUE_IZQUIERDO:
			return ALTURA_MEÑIQUE_IZQUIERDO;
		default:
			return -1;
		}
	}
	
	/*
	 * 
	 * FUNCIONES PARA LA ENTRADA/SALIDA
	 */
	
	
	/**
	 * Función auxiliar que crea un {@link JSONObject} con la información necesaria para digitar la partitura. Este objeto sera pasado al embajador para
	 * que digite la partitura
	 * @param digitacionFinal Cuerda y traste en los que tocar cada nota de la partitura
	 * @param dedosFinal dedos con los que tocar cada nota de la partitura
	 * @param mejorCoste Coste de la digitación
	 * @param numNotasYTrastes Numero de notas y acordes en la partitura
	 * @param rutaArchivoNuevo Ruta en la que se quiere guardar en MusicXML con la partitura digitada
	 * @return {@link JSONObject} con la información de la digitación.
	 */
	private JSONObject objetoDigitacionSalida(PosGuitarra[] digitacionFinal, Dedo[] dedosFinal, int mejorCoste, int numNotasYTrastes, JSONArray arraynotas) {
		JSONObject json_salida = new JSONObject();
		
		JSONArray arrayDigitacion = new JSONArray();
		
		for(int i = 0; i < digitacionFinal.length; i++) {
			arrayDigitacion.put("%d,%d,%d,%c".formatted(digitacionFinal[i].getCuerda(), digitacionFinal[i].getTraste(), 
																dedosFinal[i].getDedo_izq(), getDedoDerecho_char(dedosFinal[i].getDedo_der()), arraynotas.getString(i)));
		}
		
		json_salida.put("digitaciones", arrayDigitacion);
		json_salida.put("coste", mejorCoste);
		json_salida.put("numNotasYTrastes", numNotasYTrastes);
		
		
		
		return json_salida;
	}
	
	/**
	 * Funcion que dado un identificador de dedo de la mano derecha de la PD, devuelve el caracter correspondiente para apuntarlo en la partitura.
	 * @param dedo_der
	 * @return caracter representativo del dedo dado
	 */
	private char getDedoDerecho_char(int dedo_der) {
		switch(dedo_der) {
		case PULGAR_DERECHO:
			return CARACTER_PULGAR;
		case INDICE_DERECHO:
			return CARACTER_INDICE;
		case CORAZON_DERECHO:
			return CARACTER_CORAZON;
		case ANULAR_DERECHO:
			return CARACTER_ANULAR;
		case MEÑIQUE_DERECHO:
			return CARACTER_MEÑIQUE;
		default:
			return CARACTER_DEDO_DESCONOCIDO;
			
		}
	}
	
	
}
