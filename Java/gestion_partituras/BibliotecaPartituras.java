package gestion_partituras;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class BibliotecaPartituras {
	private HashMap<String, Partitura> partituras;
	private HashMap<String, Coleccion> colecciones;
	private JSONObject infoPartis;
	
	public BibliotecaPartituras(JSONObject infoPartituras) {
		infoPartis = infoPartituras;
		
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

	private Partitura parseaPartitura(JSONObject part_i_info) {
		Partitura part;
		
		String nombrePartitura = part_i_info.getString("nombre");
		String rutaPDF = part_i_info.getString("ruta_pdf");
		String rutaMusicXML = part_i_info.getString("ruta_xml");
		if(part_i_info.has("ruta_midi")) {
			part = new Partitura(nombrePartitura, rutaPDF, new Mi_MusicXML(rutaMusicXML), new Mi_Midi(part_i_info.getString("ruta_midi")));
		}
		else {
			part = new Partitura(nombrePartitura, rutaPDF, new Mi_MusicXML(rutaMusicXML));
		}
		return part;
		
	}
	
	public Partitura getPartitura(String nombre) {
		if(partituras.containsKey(nombre)) {
			return partituras.get(nombre);
		}
		else {
			//TODO: Lanzar excepcion ya que esto no deberia de pasar.
			return null;
		}
	}
	
	public void insertaPartitura(Partitura nuevaPartitura) {
		if(partituras.containsKey(nuevaPartitura.getNombre_partitura())) {
			//TODO: lanzar excepcion porque esto no debe pasar
		}
		else {
			partituras.put(nuevaPartitura.getNombre_partitura(), nuevaPartitura);
		}
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
	
	public void eliminaPartituras(List<String> nombres) {
		for(String nombre : nombres) {
			Partitura p = partituras.get(nombre);
			p.eliminaArchivos();
			partituras.remove(nombre);
		}
	}
	
	public void creaColeccion(List<String> nombres, String nombreColeccion) {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(!partituras.containsKey(nombre)) {
				//TODO: lanzar excepcion
			}
			partis.add(partituras.get(nombre));
		}
		if(colecciones.containsKey(nombreColeccion)) {
			//TODO: lanzar excepcion ya que existe ya una coleccion
		}
		colecciones.put(nombreColeccion, new Coleccion(nombreColeccion, partis));
	}
	
	public Coleccion getColeccion(String nombre) {
		if(!colecciones.containsKey(nombre)) {
			//TODO: Lanzar excepcion de que no existe
		}
		return colecciones.get(nombre);
	}
	
	public void eliminaColeccion(String nombre) {
		if(!colecciones.containsKey(nombre)) {
			//TODO: Lanzar excepcion de que no existe
		}
		colecciones.remove(nombre);
	}
	
	public List<Partitura> getPartituras(List<String> nombres) {
		List<Partitura> partis = new ArrayList<>(nombres.size());
		for(String nombre : nombres) {
			if(!partituras.containsKey(nombre)) {
				//TODO: lanzar excepcion de que no existe
			}
			else {
				partis.add(partituras.get(nombre));
			}
		}
		
		return partis;
	}

}
