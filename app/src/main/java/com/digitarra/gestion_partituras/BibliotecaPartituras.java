package com.digitarra.gestion_partituras;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import com.digitarra.digitacion.AcordeLongitudImposibleException;
import com.digitarra.digitacion.Digitador;
import com.digitarra.digitacion.NotaDesconocidaException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

	private Path pathXMLs;

	private Path pathPDFs;

	private Path pathTemps;

	private Context context;

	private GeneradorPDF generadorPDFs;

	private static final String RUTA_RELATIVA_JSON = "metadatos.json";
	
	public BibliotecaPartituras(Context contexto) throws JSONException, IOException {
		context = contexto;
		pathApp = contexto.getFilesDir().toPath();
		generadorPDFs = new GeneradorPDF(context);

		partituras = new HashMap<>(10);
		colecciones = new HashMap<>(10);

		Path pathJSON = pathApp.resolve(RUTA_RELATIVA_JSON);

		pathXMLs = pathApp.resolve("MusicXML_Files");

		pathTemps = pathApp.resolve("temp");

		pathPDFs = pathApp.resolve("PDFs");

		if(!(Files.exists(pathXMLs) && Files.isDirectory(pathXMLs))) {
			Files.createDirectories(pathXMLs);
		}

		if(!(Files.exists(pathTemps) && Files.isDirectory(pathTemps))) {
			Files.createDirectories(pathTemps);
		}

		if(!(Files.exists(pathPDFs) && Files.isDirectory(pathPDFs))) {
			Files.createDirectories(pathPDFs);
		}


		if(Files.exists(pathJSON)) {
			String contenido = new String(Files.readAllBytes(pathJSON), StandardCharsets.UTF_8);

			JSONObject infoPartituras = new JSONObject(contenido);

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


	}



	private Partitura parseaPartitura(JSONObject part_i_info) throws JSONException {
		Partitura part;
		
		String nombrePartitura = part_i_info.getString("nombre");
		Path rutaPDF = pathApp.resolve(part_i_info.getString("ruta_pdf"));
		Path rutaMusicXML = pathApp.resolve(part_i_info.getString("ruta_xml"));
		if(part_i_info.getBoolean("digitada")) {
			part = new PartituraDigitada(nombrePartitura, rutaPDF, rutaMusicXML);
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
//			if(!PartituraDigitada.class.isInstance(p)) {
			if(!p.isDigitada()) {
				resul.add(p);
			}
		}
		return resul;
	}

	public void digitaPartitura(String nombrePartitura) throws NombrePartituraEnUsoException, PartituraNoExisteException, NotaDesconocidaException, JSONException, IOException, InterruptedException, AcordeLongitudImposibleException {
		Digitador digit = new Digitador();

		Partitura partituraSinDigitar = this.getPartitura(nombrePartitura);

		System.out.println("000");

		PartituraDigitada resul = digit.digitaConAcordes(partituraSinDigitar, context);

		System.out.println("aaa");
		this.insertaPartitura(resul);
	}
	
	public List<Partitura> getPartiturasDigitadas() {
		List<Partitura> resul = new ArrayList<Partitura>(partituras.size());
		for(Partitura p : partituras.values()) {
//			if(PartituraDigitada.class.isInstance(p)) {
			if(p.isDigitada()) {
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

	public void eliminaPartitura(String nombrePartitura) throws ArchivoNoSePudoBorrarException, PartituraNoExisteException {
		Partitura p = this.getPartitura(nombrePartitura);
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
		partituras.remove(nombrePartitura);
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

	public List<Partitura> getPartiturasSinColeccion() {
		List<Partitura> resul = new ArrayList<>(10);
		for(Partitura p : partituras.values()) {
			boolean estaEnColeccion = false;
			for(Coleccion c : colecciones.values()) {
				if(c.contienePartitura(p.getNombre_partitura())) {
					estaEnColeccion = true;
				}
			}
			if(!estaEnColeccion) {
				resul.add(p);
			}
		}
		return resul;
	}

	public List<String> getNombresColecciones() {
		return new ArrayList<>(colecciones.keySet());
	}

	public List<Coleccion> getAllColecciones() {
		return new ArrayList<>(colecciones.values());
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
			objetoPartitura.put("digitada", partitura.isDigitada());

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

	public Partitura nuevaPartitura(Uri uri) throws IOException, NombrePartituraEnUsoException, ArchivoNoSePudoBorrarException {
		String nombre = obtenerNombreDesdeUri(uri);
		Path rutaArchivoAux = pathTemps.resolve(nombre);
		try (InputStream inputStream = context.getContentResolver().openInputStream(uri)) {
			if (inputStream == null) {
				throw new IOException("No se pudo abrir el archivo origen.");
			}

			// Copia directa del Stream al Path de destino (reemplaza si ya existe)
			Files.copy(inputStream, rutaArchivoAux, StandardCopyOption.REPLACE_EXISTING);
		}



		EmbajadorMusic21Python embajador = new EmbajadorMusic21Python(context);

		String nombreSinExtension = nombre.substring(0, nombre.lastIndexOf("."));

		Path pathXMLNuevo = pathXMLs.resolve(nombreSinExtension+".xml");

		System.out.println(nombreSinExtension);

		Path rutaXMLBueno = Paths.get(embajador.convierteAMusicXML(rutaArchivoAux, pathXMLNuevo));

		//Path rutaPDF = Paths.get(generadorPDFs.obtenerPDF(rutaXMLBueno.toString()));
		Path rutaPDF = Paths.get(generadorPDFs.obtenerPDF(rutaXMLBueno.toString(), pathPDFs.resolve(nombreSinExtension+".pdf").toString()));

		Partitura part = new Partitura(nombreSinExtension, rutaPDF, rutaXMLBueno);
		this.insertaPartitura(part);

//		if(!rutaArchivoAux.toFile().delete()) {
//			throw new ArchivoNoSePudoBorrarException(rutaArchivoAux.toString());
//		}

		return part;
	}

	private String obtenerNombreDesdeUri(Uri uri) {
		String nombre = null;

		// Si el Uri es de tipo content:// (el estándar en SAF y MediaStore)
		if ("content".equals(uri.getScheme())) {
			try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
				if (cursor != null && cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
					if (index != -1) {
						nombre = cursor.getString(index);
					}
				}
			}
		}

		// Si la consulta no devuelve nombre o si el Uri es de tipo file:// (rutas antiguas)
		if (nombre == null) {
			nombre = uri.getPath();
			int cut = nombre.lastIndexOf('/');
			if (cut != -1) {
				nombre = nombre.substring(cut + 1);
			}
		}

		return nombre;
	}

	public void digitar(Partitura part) throws NombrePartituraEnUsoException, NotaDesconocidaException, JSONException, IOException, InterruptedException {
		Digitador digit = new Digitador();
		Partitura partNueva = digit.digita(part, context);
		this.insertaPartitura(partNueva);
	}


	public void editaPartitura(String nombrePartitura, JSONObject cambiosPartitura) throws PartituraNoExisteException, ArchivoNoSePudoBorrarException {
		Partitura part = this.getPartitura(nombrePartitura);

		EmbajadorMusic21Python embajador = new EmbajadorMusic21Python(context);

		embajador.editaPartitura(part.getPartitura_MusicXML().getRuta(), cambiosPartitura);

		//String rutaPDFNueva = GeneradorPDF.obtenerPDF(part.getPartitura_MusicXML().getRuta().toString());
		generadorPDFs.obtenerPDF(part.getPartitura_MusicXML().getRuta().toString(), part.getRutaPDF().toString());
		//part.setRutaPDF(pathApp.resolve(rutaPDFNueva));
//		part.setMi_MusicXML();

	}

}
