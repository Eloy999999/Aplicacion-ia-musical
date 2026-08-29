package com.example.digitarra.Java.gestion_partituras;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BibliotecaPartituras {
	private HashMap<String, Partitura> partituras;
	private HashMap<String, Coleccion> colecciones;
	private JSONObject infoPartis;

	public BibliotecaPartituras(JSONObject infoPartituras) throws JSONException {
		infoPartis = infoPartituras;

		if (this.partituras == null) {
			this.partituras = new HashMap<>();
		}
		if (this.colecciones == null) {
			this.colecciones = new HashMap<>();
		}

		JSONArray infoPartiturasIndividual = infoPartituras.getJSONArray("partituras");

		for(int i = 0; i < infoPartiturasIndividual.length(); i++) {
			JSONObject part_i_info = infoPartiturasIndividual.getJSONObject(i);
			Partitura partitura = parseaPartitura(part_i_info);

			partituras.put(partitura.getNombre_partitura(), partitura);
		}

		JSONArray infoColecciones = infoPartituras.getJSONArray("colecciones");

		for(int i = 0; i < infoColecciones.length(); i++) {
			JSONObject colec_i_info = infoColecciones.getJSONObject(i);
			JSONArray nombrePartituras = colec_i_info.getJSONArray("nombres_partituras");
			List<Partitura> listaPartituras = new ArrayList<Partitura>();
			for(int j = 0; j < nombrePartituras.length(); j++) {
				listaPartituras.add(partituras.get(nombrePartituras.getString(j)));
			}
			Coleccion colecc = new Coleccion(colec_i_info.getString("nombre"), listaPartituras);
			colecciones.put(colecc.getNombre(), colecc);
		}
	}

	private Partitura parseaPartitura(JSONObject part_i_info) throws JSONException {
		Partitura part;

		String nombrePartitura = part_i_info.getString("nombre");
		String rutaPDF = part_i_info.getString("ruta_pdf");
		String rutaMusicXML = part_i_info.getString("ruta_xml");

		// Lee el booleano 'digitada'. Si no existe en el JSON, asigna false por defecto
		boolean digitada = part_i_info.optBoolean("digitada", false);

		if(part_i_info.has("ruta_midi")) {
			part = new Partitura(nombrePartitura, rutaPDF, rutaMusicXML, part_i_info.getString("ruta_midi"), digitada);
		}
		else {
			part = new Partitura(nombrePartitura, rutaPDF, rutaMusicXML, digitada);
		}
		return part;
	}

	public Partitura getPartitura(String nombre) {
		if(partituras.containsKey(nombre)) {
			return partituras.get(nombre);
		}
		else {
			return null;
		}
	}

	public void insertaPartitura(Partitura nuevaPartitura) {
		if (nuevaPartitura == null) return;

		// 1. Añadir al HashMap en memoria
		if (!partituras.containsKey(nuevaPartitura.getNombre_partitura())) {
			partituras.put(nuevaPartitura.getNombre_partitura(), nuevaPartitura);
		}

		// 2. Sincronizar con el JSONObject interno (infoPartis)
		try {
			JSONArray partiturasArray = infoPartis.optJSONArray("partituras");
			if (partiturasArray == null) {
				partiturasArray = new JSONArray();
				infoPartis.put("partituras", partiturasArray);
			}

			// Verificar si ya existe en el JSONArray para actualizar o añadir
			JSONObject objPartitura = null;
			for (int i = 0; i < partiturasArray.length(); i++) {
				JSONObject pObj = partiturasArray.getJSONObject(i);
				if (pObj.getString("nombre").equals(nuevaPartitura.getNombre_partitura())) {
					objPartitura = pObj;
					break;
				}
			}

			// Si no existe, crear uno nuevo
			if (objPartitura == null) {
				objPartitura = new JSONObject();
				objPartitura.put("nombre", nuevaPartitura.getNombre_partitura());
				partiturasArray.put(objPartitura);
			}

			// Asignar las rutas requeridas y el flag digitada
			objPartitura.put("ruta_pdf", nuevaPartitura.getRutaPDF());
			objPartitura.put("digitada", nuevaPartitura.isDigitada());

			if (nuevaPartitura.getPartitura_MusicXML() != null) {
				objPartitura.put("ruta_xml", nuevaPartitura.getPartitura_MusicXML().getRuta());
			}

			if (nuevaPartitura.getPartitura_Midi() != null) {
				objPartitura.put("ruta_midi", nuevaPartitura.getPartitura_Midi().getRuta());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public List<Partitura> getAllPartituras() {
		return new ArrayList<Partitura>(partituras.values());
	}

	public List<Partitura> getPartiturasSinDigitar() {
		List<Partitura> resul = new ArrayList<Partitura>(partituras.size());
		for(Partitura p : partituras.values()) {
			if(!p.isDigitada()) {
				resul.add(p);
			}
		}
		return resul;
	}

	public List<Partitura> getPartiturasDigitadas() {
		List<Partitura> resul = new ArrayList<Partitura>(partituras.size());
		for(Partitura p : partituras.values()) {
			if(p.isDigitada()) {
				resul.add(p);
			}
		}
		return resul;
	}

	public void eliminaPartituras(List<String> nombres) {
		for(String nombre : nombres) {
			Partitura p = partituras.get(nombre);
			if (p != null) {
				p.eliminaArchivos();
				partituras.remove(nombre);
			}
		}
	}

	// ==========================================
	// MÉTODOS DE CREACIÓN Y AÑADIDO DE COLECCIONES
	// ==========================================

	// Añade un objeto Coleccion creado externamente y actualiza el JSON
	public void addColeccion(Coleccion nuevaColeccion) {
		if (nuevaColeccion != null && !colecciones.containsKey(nuevaColeccion.getNombre())) {
			colecciones.put(nuevaColeccion.getNombre(), nuevaColeccion);
			sincronizaColeccionEnJson(nuevaColeccion);
		}
	}

	// Crea una colección vacía solo con el nombre
	public void creaColeccionVacia(String nombreColeccion) {
		if (!colecciones.containsKey(nombreColeccion)) {
			Coleccion nueva = new Coleccion(nombreColeccion, new ArrayList<>());
			colecciones.put(nombreColeccion, nueva);
			sincronizaColeccionEnJson(nueva);
		}
	}

	// Crea una colección con lista de nombres de partituras
	public void creaColeccion(List<String> nombres, String nombreColeccion) {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(partituras.containsKey(nombre)) {
				partis.add(partituras.get(nombre));
			}
		}
		if(!colecciones.containsKey(nombreColeccion)) {
			Coleccion nueva = new Coleccion(nombreColeccion, partis);
			colecciones.put(nombreColeccion, nueva);
			sincronizaColeccionEnJson(nueva);
		}
	}

	// Método auxiliar privado para mantener actualizado el JSONObject interno
	private void sincronizaColeccionEnJson(Coleccion coleccion) {
		try {
			JSONArray coleccionesArray = infoPartis.optJSONArray("colecciones");
			if (coleccionesArray == null) {
				coleccionesArray = new JSONArray();
				infoPartis.put("colecciones", coleccionesArray);
			}

			// Buscamos si la colección ya existe para actualizarla en lugar de duplicarla
			JSONObject objColeccion = null;
			for (int i = 0; i < coleccionesArray.length(); i++) {
				JSONObject col = coleccionesArray.getJSONObject(i);
				if (col.getString("nombre").equals(coleccion.getNombre())) {
					objColeccion = col;
					break;
				}
			}

			// Si no existe, creamos un objeto nuevo y lo metemos al array
			if (objColeccion == null) {
				objColeccion = new JSONObject();
				objColeccion.put("nombre", coleccion.getNombre());
				coleccionesArray.put(objColeccion);
			}

			// Actualizamos los nombres de las partituras
			JSONArray arrayNombres = new JSONArray();
			if (coleccion.getPartituras() != null) {
				for (Partitura p : coleccion.getPartituras()) {
					arrayNombres.put(p.getNombre_partitura());
				}
			}
			objColeccion.put("nombres_partituras", arrayNombres);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public Coleccion getColeccion(String nombre) {
		return colecciones.get(nombre);
	}

	public List<Partitura> getPartituras(List<String> nombres) {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(partituras.containsKey(nombre)) {
				partis.add(partituras.get(nombre));
			}
		}
		return partis;
	}

	public List<Coleccion> getAllColecciones() {
		return new ArrayList<>(colecciones.values());
	}

	public List<Partitura> getPartiturasSueltas() {
		List<Partitura> sueltas = new ArrayList<>();

		// Obtener nombres de todas las partituras que están dentro de alguna colección
		List<String> nombresEnColecciones = new ArrayList<>();
		for (Coleccion c : colecciones.values()) {
			if (c.getPartituras() != null) {
				for (Partitura p : c.getPartituras()) {
					nombresEnColecciones.add(p.getNombre_partitura().trim().toLowerCase());
				}
			}
		}

		// Filtrar la lista general
		for (Partitura p : partituras.values()) {
			if (!nombresEnColecciones.contains(p.getNombre_partitura().trim().toLowerCase())) {
				sueltas.add(p);
			}
		}

		return sueltas;
	}

	public void eliminaColeccion(String nombre) {
		if (colecciones.containsKey(nombre)) {
			colecciones.remove(nombre);

			JSONArray coleccionesArray = infoPartis.optJSONArray("colecciones");
			if (coleccionesArray != null) {
				JSONArray nuevoArray = new JSONArray();
				for (int i = 0; i < coleccionesArray.length(); i++) {
					try {
						JSONObject colObj = coleccionesArray.getJSONObject(i);
						if (!colObj.getString("nombre").equals(nombre)) {
							nuevoArray.put(colObj);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				try {
					infoPartis.put("colecciones", nuevoArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void guardarCambiosEnJson(android.content.Context context) {
		try {
			// Guardar el JSONObject modificado en Partituras.json dentro de filesDir
			File file = new File(context.getFilesDir(), "Partituras.json");
			FileWriter writer = new FileWriter(file);
			writer.write(infoPartis.toString(4));
			writer.flush();
			writer.close();
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
	}

	public void agregarPartiturasAColeccion(String nombreColeccion, List<Partitura> nuevasPartituras) {
		Coleccion coleccion = colecciones.get(nombreColeccion);
		if (coleccion == null || nuevasPartituras == null) return;

		for (Partitura pSeleccionada : nuevasPartituras) {
			String nombreBuscado = pSeleccionada.getNombre_partitura();

			// 1. Buscamos el objeto original en la biblioteca general
			Partitura partituraReal = partituras.get(nombreBuscado);
			if (partituraReal == null) {
				for (Partitura p : partituras.values()) {
					if (p.getNombre_partitura().trim().equalsIgnoreCase(nombreBuscado.trim())) {
						partituraReal = p;
						break;
					}
				}
			}

			// 2. Si existe, la añadimos directamente al HashMap de la colección
			if (partituraReal != null) {
				coleccion.añadePartitura(partituraReal.getNombre_partitura(), partituraReal);
			}
		}

		// 3. Sincronizamos con el JSONObject interno
		sincronizarColeccionEnJsonObject(nombreColeccion);
	}

	public void quitarPartiturasDeColeccion(String nombreColeccion, List<Partitura> partiturasAQuitar) {
		Coleccion coleccion = colecciones.get(nombreColeccion);
		if (coleccion == null || partiturasAQuitar == null) return;

		for (Partitura aQuitar : partiturasAQuitar) {
			coleccion.quitarPartitura(aQuitar.getNombre_partitura());
		}

		sincronizarColeccionEnJsonObject(nombreColeccion);
	}

	public void sincronizarEstructuraJson() {
		try {
			JSONArray coleccionesArray = new JSONArray();

			for (Coleccion c : colecciones.values()) {
				JSONObject colObj = new JSONObject();
				colObj.put("nombre", c.getNombre());

				JSONArray nombresArray = new JSONArray();
				if (c.getPartituras() != null) {
					for (Partitura p : c.getPartituras()) {
						nombresArray.put(p.getNombre_partitura());
					}
				}
				colObj.put("nombres_partituras", nombresArray);
				coleccionesArray.put(colObj);
			}

			infoPartis.put("colecciones", coleccionesArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void sincronizarColeccionEnJsonObject(String nombreColeccion) {
		try {
			Coleccion coleccion = colecciones.get(nombreColeccion);
			if (coleccion == null) return;

			JSONArray coleccionesArray = infoPartis.optJSONArray("colecciones");
			if (coleccionesArray != null) {
				for (int i = 0; i < coleccionesArray.length(); i++) {
					JSONObject colObj = coleccionesArray.getJSONObject(i);
					if (colObj.getString("nombre").equalsIgnoreCase(nombreColeccion)) {

						JSONArray nuevoNombresArray = new JSONArray();
						if (coleccion.getPartituras() != null) {
							for (Partitura p : coleccion.getPartituras()) {
								nuevoNombresArray.put(p.getNombre_partitura());
							}
						}
						colObj.put("nombres_partituras", nuevoNombresArray);
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void eliminarPartitura(String nombrePartitura) {
		Partitura p = partituras.get(nombrePartitura);
		if (p == null) return;

		// 1. Elimina archivos internos de la app (PDF, XML, MIDI)
		p.eliminaArchivos();

		// 2. Elimina de la memoria interna
		partituras.remove(nombrePartitura);

		// 3. Elimina de las colecciones en memoria
		for (Coleccion c : colecciones.values()) {
			if (c.getPartituras() != null) {
				c.quitarPartitura(nombrePartitura);
			}
		}

		// 4. Elimina del JSON en memoria (infoPartis)
		try {
			JSONArray partiturasArray = infoPartis.optJSONArray("partituras");
			if (partiturasArray != null) {
				JSONArray nuevoArray = new JSONArray();
				for (int i = 0; i < partiturasArray.length(); i++) {
					JSONObject pObj = partiturasArray.getJSONObject(i);
					if (!pObj.getString("nombre").equalsIgnoreCase(nombrePartitura)) {
						nuevoArray.put(pObj);
					}
				}
				infoPartis.put("partituras", nuevoArray);
			}

			JSONArray coleccionesArray = infoPartis.optJSONArray("colecciones");
			if (coleccionesArray != null) {
				for (int i = 0; i < coleccionesArray.length(); i++) {
					JSONObject colObj = coleccionesArray.getJSONObject(i);
					JSONArray nombresArray = colObj.optJSONArray("nombres_partituras");
					if (nombresArray != null) {
						JSONArray nuevoNombresArray = new JSONArray();
						for (int j = 0; j < nombresArray.length(); j++) {
							String nombreActual = nombresArray.getString(j);
							if (!nombreActual.equalsIgnoreCase(nombrePartitura)) {
								nuevoNombresArray.put(nombreActual);
							}
						}
						colObj.put("nombres_partituras", nuevoNombresArray);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}