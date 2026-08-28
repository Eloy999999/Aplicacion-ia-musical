package com.example.digitarra.Java.gestion_partituras;

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
	
	public void añadePartitura(String nombre, Partitura part) {
		if(partituras.containsKey(nombre)) {
			//TODO: lanzar excepcion porque ya estaba añadida
		}
		else {
			partituras.put(nombre, part);
		}
	}
	
	public void quitarPartitura(String nombre) {
		if(!partituras.containsKey(nombre)) {
			//TODO: lanzar excepcion de no existia
		}
		else {
			partituras.remove(nombre);
		}
	}
	
	public Partitura getPartitura(String nombre) {
		if(!partituras.containsKey(nombre)) {
			//TODO: lanzar excepcion porque ya estaba añadida
		}
		return partituras.get(nombre);
	}

	public List<Partitura> getPartituras() {
		return new ArrayList<>(partituras.values());
	}
	
	public List<Partitura> getAllPartituras() {
		return new ArrayList<Partitura>(partituras.values());
	}

	public void setPartituras(List<Partitura> nuevasPartituras) {
		this.partituras = new HashMap<>();
		if (nuevasPartituras != null) {
			for (Partitura p : nuevasPartituras) {
				if (p != null) {
					this.partituras.put(p.getNombre_partitura(), p);
				}
			}
		}
	}


}
