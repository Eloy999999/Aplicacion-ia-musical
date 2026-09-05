package com.digitarra.gestion_partituras;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Coleccion {
	private String nombreColeccion;
	private HashMap<String, Partitura> partituras;

	public Coleccion(String nomCol, List<Partitura> listaPartituras) {
		nombreColeccion = nomCol;
		partituras = new HashMap<>(listaPartituras.size());
		for(Partitura p : listaPartituras) {
			partituras.put(p.getNombre_partitura(), p);
		}
	}
	
	public String getNombre() {
		return nombreColeccion;
	}
	
	public void añadePartitura(String nombre, Partitura part) throws NombrePartituraEnUsoException {
		if(partituras.containsKey(nombre)) {
			throw new NombrePartituraEnUsoException(nombre);
		}
		else {
			partituras.put(nombre, part);
		}
	}

	public List<String> getNombresPartituras() {
		return new ArrayList<>(partituras.keySet());
	}
	
	public void quitarPartitura(String nombre) throws PartituraNoExisteException {
		if(!partituras.containsKey(nombre)) {
			throw new PartituraNoExisteException(nombre);
		}
		else {
			partituras.remove(nombre);
		}
	}

	public boolean contienePartitura(String nombre) {
		return partituras.containsKey(nombre);
	}
	
	public Partitura getPartitura(String nombre) throws PartituraNoExisteException {
		if(!partituras.containsKey(nombre)) {
			throw new PartituraNoExisteException(nombre);
		}
		return partituras.get(nombre);
	}
	
	public List<Partitura> getAllPartituras() {
		return new ArrayList<Partitura>(partituras.values());
	}

	public void añadePartituras(List<Partitura> partituras) throws NombrePartituraEnUsoException {
		for(Partitura p : partituras) {
			this.añadePartitura(p.getNombre_partitura(), p);
		}
	}

	public void quitarPartituras(List<Partitura> partituras) throws PartituraNoExisteException {
		for(Partitura p : partituras) {
			this.quitarPartitura(p.getNombre_partitura());
		}
	}
}
