package gestion_partituras;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BibliotecaPartituras {
	private HashMap<String, Partitura> partituras;
	private HashMap<String, Coleccion> colecciones;

	private Path pathApp;

	private Context context;

	private static final String RUTA_RELATIVA_JSON = "metadatos.json";
	
	public BibliotecaPartituras(JSONObject infoPartituras, Context contexto) throws JSONException {
		context = contexto;
		pathApp = contexto.getFilesDir().toPath();

		partituras = new HashMap<>(10);
		colecciones = new HashMap<>(10);

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
		Path rutaPDF = pathApp.resolve(part_i_info.getString("ruta_pdf"));
		Path rutaMusicXML = pathApp.resolve(part_i_info.getString("ruta_xml"));
		if(part_i_info.has("ruta_midi")) {
			part = new Partitura(nombrePartitura, rutaPDF, rutaMusicXML, pathApp.resolve(part_i_info.getString("ruta_midi")));
		}
		else {
			part = new Partitura(nombrePartitura, rutaPDF, rutaMusicXML);
		}
		return part;
		
	}
	
	public Partitura getPartitura(String nombre) throws PartituraNoExisteException {
		if(!partituras.containsKey(nombre)) {
			throw new PartituraNoExisteException(nombre);
		}
		return partituras.get(nombre);
	}
	
	public void insertaPartitura(Partitura nuevaPartitura) throws NombrePartituraEnUsoException {
		if(partituras.containsKey(nuevaPartitura.getNombre_partitura())) {
			throw new NombrePartituraEnUsoException(nuevaPartitura.getNombre_partitura());
		}
		partituras.put(nuevaPartitura.getNombre_partitura(), nuevaPartitura);
	}
	
	public List<Partitura> getAllPartituras() {
		List<Partitura> partis = new ArrayList<Partitura>(partituras.values());
		return partis;
	}
	
	public List<Partitura> getPartiturasSinDigitar() {
		List<Partitura> resul = new ArrayList<Partitura>(partituras.size());
		for(Partitura p : partituras.values()) {
			if(!PartituraDigitada.class.isInstance(p)) {
				resul.add(p);
			}
		}
		return resul;
	}
	
	public List<Partitura> getPartiturasDigitadas() {
		List<Partitura> resul = new ArrayList<Partitura>(partituras.size());
		for(Partitura p : partituras.values()) {
			if(PartituraDigitada.class.isInstance(p)) {
				resul.add(p);
			}
		}
		return resul;
	}
	
	public void eliminaPartituras(List<String> nombres) throws ArchivoNoSePudoBorrarException {
		for(String nombre : nombres) {
			Partitura p = partituras.get(nombre);
//			p.eliminaArchivos();
			File f = p.getRutaPDF().toFile();
			boolean seElimino = f.delete();
			if(!seElimino) {
				throw new ArchivoNoSePudoBorrarException(p.getRutaPDF().getFileName().toString());
			}
			f = p.getPartitura_MusicXML().getRuta().toFile();

			seElimino = f.delete();
			if(!seElimino) {
				throw new ArchivoNoSePudoBorrarException(p.getPartitura_MusicXML().getRuta().getFileName().toString());
			}
			partituras.remove(nombre);
		}
	}
	
	public void creaColeccion(List<String> nombres, String nombreColeccion) throws NombreColeccionEnUsoException, PartituraNoExisteException {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(!partituras.containsKey(nombre)) {
				throw new PartituraNoExisteException(nombre);
			}
			partis.add(partituras.get(nombre));
		}
		if(colecciones.containsKey(nombreColeccion)) {
			throw new NombreColeccionEnUsoException(nombreColeccion);
		}
		colecciones.put(nombreColeccion, new Coleccion(nombreColeccion, partis));
	}
	
	public Coleccion getColeccion(String nombre) throws NombreColeccionNoExisteException {
		if(!colecciones.containsKey(nombre)) {
			throw new NombreColeccionNoExisteException(nombre);
		}
		return colecciones.get(nombre);
	}
	
	public void eliminaColeccion(String nombre) throws NombreColeccionNoExisteException {
		if(!colecciones.containsKey(nombre)) {
			throw new NombreColeccionNoExisteException(nombre);
		}
		colecciones.remove(nombre);
	}
	
	public List<Partitura> getPartituras(List<String> nombres) throws PartituraNoExisteException {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(!partituras.containsKey(nombre)) {
				throw new PartituraNoExisteException(nombre);
			}
			else {
				partis.add(partituras.get(nombre));
			}
		}
		
		return partis;
	}

	public List<String> getNombresPartiturasSinColeccion() {
		List<String> resul = new ArrayList<>(10);
		for(Partitura p : partituras.values()) {
			boolean estaEnColeccion = false;
			for(Coleccion c : colecciones.values()) {
				if(c.contienePartitura(p.getNombre_partitura())) {
					estaEnColeccion = true;
				}
			}
			if(!estaEnColeccion) {
				resul.add(p.getNombre_partitura());
			}
		}
		return resul;
	}



	public List<String> getNombresColecciones() {
		return new ArrayList<>(colecciones.keySet());
	}
	
	public void cierraBiblioteca() throws JSONException, IOException {
		JSONObject jsonActualizado = new JSONObject();
		JSONArray arrayPartituras = new JSONArray();
		//guardamos las partituras que hay actualmente
		for(String nombre : partituras.keySet()) {
			Partitura partitura = partituras.get(nombre);
			JSONObject objetoPartitura = new JSONObject();
			objetoPartitura.put("nombre", nombre);
			objetoPartitura.put("ruta_pdf", pathApp.relativize(partitura.getRutaPDF()).toString());
			objetoPartitura.put("ruta_xml", pathApp.relativize(partitura.getPartitura_MusicXML().getRuta()).toString());

			arrayPartituras.put(objetoPartitura);
		}
		jsonActualizado.put("partituras", arrayPartituras);

		JSONArray arrayColecciones = new JSONArray();

		for(String nombreColeccion : colecciones.keySet()) {
			Coleccion coleccion = colecciones.get(nombreColeccion);
			JSONObject objetoColeccionI = new JSONObject();
			objetoColeccionI.put("nombre", coleccion.getNombre());
			JSONArray nombresPartiturasColeccion = new JSONArray();
			for(Partitura nombrePartitura : coleccion.getAllPartituras()) {
				nombresPartiturasColeccion.put(nombrePartitura);
			}
			objetoColeccionI.put("nombres_partituras", nombresPartiturasColeccion);

			arrayColecciones.put(objetoColeccionI);
		}

		jsonActualizado.put("colecciones", arrayColecciones);

		File archivoSalida = pathApp.resolve(RUTA_RELATIVA_JSON).toFile();
		try (FileWriter fout = new FileWriter(archivoSalida)) {
			fout.write(jsonActualizado.toString(4));
		}
	}

}
