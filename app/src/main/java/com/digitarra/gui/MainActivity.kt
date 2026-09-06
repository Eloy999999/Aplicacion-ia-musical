package com.digitarra.gui

import android.content.Context
import android.net.Uri
import com.digitarra.app_tfg.R
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.digitarra.gestion_partituras.BibliotecaPartituras
import com.digitarra.gestion_partituras.Coleccion
import com.digitarra.gestion_partituras.DigitacionNota
import com.digitarra.gestion_partituras.NombreColeccionEnUsoException
import com.digitarra.gestion_partituras.Partitura
import kotlinx.coroutines.launch

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private var bibliotecaInstancia: BibliotecaPartituras? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        val context = this
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var pantallaActual by remember { mutableStateOf(0) }
                var biblioteca by remember { mutableStateOf<BibliotecaPartituras?>(null) }
                var coleccionActiva by remember { mutableStateOf<Coleccion?>(null) }

                var partituraAEditar by remember { mutableStateOf<Partitura?>(null) }
                var listaDigitacionesAEditar by remember { mutableStateOf<List<DigitacionNota>>(emptyList()) }

                // Variable de estado para forzar el redibujado de Compose al modificar la biblioteca
                var refrescoKey by remember { mutableIntStateOf(0) }

                var uriSeleccionada by remember {mutableStateOf<Uri?>(null)}

                //val context = LocalContext.current

                LaunchedEffect(biblioteca) {
                    bibliotecaInstancia = biblioteca
                }

                // Registrar el launcher para seleccionar archivos XML o MIDI
                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    uriSeleccionada = uri
//                    uri?.let {
//                        try {
//                            //val nombreOriginal = obtenerNombreArchivo(it)
//                            //val extension = nombreOriginal.substringAfterLast(".").lowercase()
//
//
//
//                            Toast.makeText(
//                                context,
//                                "Generando PDF, por favor espera...",
//                                Toast.LENGTH_SHORT
//                            ).show()
//
//                            val creadorPartituras = CreadorPartituras(this)
//
//                            lifecycleScope.launch {
//
//                                // Le pasas la URI directamente sin importar si es XML o MIDI
//                                val resultado = creadorPartituras.procesarYGuardarPartitura(uri, biblioteca)
//
//
//
//                                resultado.fold(
//                                    onSuccess = {
//                                        val temp = biblioteca
//                                        biblioteca = null
//                                        biblioteca = temp
//                                        refrescoKey++
//                                        pantallaActual = 1
//                                        Toast.makeText(
//                                            this@MainActivity,
//                                            "Partitura añadida con éxito",
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    },
//                                    onFailure = { error ->
//                                        Toast.makeText(
//                                            this@MainActivity,
//                                            "Error: ${error.message}",
//                                            Toast.LENGTH_LONG
//                                        ).show()
//                                    }
//                                )
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Toast.makeText(
//                                context,
//                                "Error al procesar el archivo: ${e.message}",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    }
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (pantallaActual) {
                            0 -> {
                                PantallaInicio(
                                    onComenzarClick = {
                                        try {
//                                            val file = File(context.filesDir, "Partituras.json")
//
//                                            // Lee de la memoria interna si ya fue modificado, o del asset si es la primera vez
//                                            val jsonContent = if (file.exists()) {
//                                                file.readText()
//                                            } else {
//                                                context.assets.open("Biblioteca/Partituras.json")
//                                                    .bufferedReader()
//                                                    .use { it.readText() }
//                                            }
//
//                                            val jsonObject = JSONObject(jsonContent)
                                            biblioteca = BibliotecaPartituras(context)
                                            pantallaActual = 1
                                        }
                                        catch (e: Exception) {
                                            //e.printStackTrace()
                                            Toast.makeText(
                                                context,
                                                "Error al cargar la biblioteca: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                )
                            }
                            1 -> {
                                key(refrescoKey) {
                                    PantallaBiblioteca(
                                        biblioteca = biblioteca,
                                        coleccionSeleccionada = coleccionActiva,
                                        onSeleccionarColeccion = { coleccion -> coleccionActiva = coleccion },
                                        onAgregarClick = { pantallaActual = 2 },
                                        onCrearColeccion = { nombre ->
                                            try {
                                                biblioteca?.creaColeccion(
                                                    emptyList<String>(),
                                                    nombre
                                                )
                                                refrescoKey++
                                            } catch (e: NombreColeccionEnUsoException) {
                                                Toast.makeText(
                                                    context,
                                                    "La coleccion \"${e.nombreColeccion}\" ya existe",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        },
                                        onAgregarPartiturasAColeccion = { seleccionadas ->
                                            coleccionActiva?.let { col ->
                                                if (seleccionadas.isNotEmpty()) {
                                                    // 1. Añadir partituras en Java y sincronizar JSONObject interno
                                                    biblioteca?.getColeccion(col.nombre)
                                                        ?.añadePartituras(seleccionadas)

                                                    // 2. Escribir el JSONObject actualizado al archivo Partituras.json en disco
                                                    //biblioteca?.guardarCambiosEnJson(context)

                                                    // 3. IMPORTANTE: Recargar la instancia de la colección activa desde memoria
                                                    coleccionActiva = biblioteca?.getColeccion(col.nombre)

                                                    // 4. Redibujar Compose
                                                    refrescoKey++
                                                }
                                            }
                                        },
                                        onQuitarPartiturasDeColeccion = { partiturasAQuitar ->
                                            coleccionActiva?.let { col ->
                                                // 1. Guardar cambios
//                                                biblioteca?.quitarPartiturasDeColeccion(col.nombre, partiturasAQuitar)
                                                col.quitarPartituras(partiturasAQuitar)
//                                                biblioteca?.guardarCambiosEnJson(context)

                                                // 2. Refrescar la referencia activa
                                                coleccionActiva = biblioteca?.getColeccion(col.nombre)

                                                // 3. Incrementar la clave de refresco
                                                refrescoKey++
                                            }
                                        },
                                        onBorrarColeccion = { coleccion ->
                                            biblioteca?.eliminaColeccion(coleccion.nombre)
//                                            biblioteca?.guardarCambiosEnJson(context)
                                            if (coleccionActiva == coleccion) coleccionActiva = null
                                            refrescoKey++
                                        },

                                        onVisualizarPartitura = { partitura ->
                                            Toast.makeText(context, "Visualizando: ${partitura.nombre_partitura}", Toast.LENGTH_SHORT).show()
                                            val visualizador = VisorPDF(this@MainActivity)
                                            visualizador.visualizarPDF(partitura)
                                        },
                                        onDigitarPartitura = { partitura ->
                                            Toast.makeText(context, "Digitando partitura, por favor espera...", Toast.LENGTH_SHORT).show()

                                            lifecycleScope.launch(Dispatchers.IO) {
                                                try {

                                                    biblioteca?.digitaPartitura(partitura.nombre_partitura)

                                                    withContext(Dispatchers.Main) {
                                                        val temp = biblioteca
                                                        biblioteca = null
                                                        biblioteca = temp

                                                        coleccionActiva?.let { col ->
                                                            coleccionActiva = biblioteca?.getColeccion(col.nombre)
                                                        }

                                                        refrescoKey++
                                                        Toast.makeText(this@MainActivity, "Partitura digitada con éxito", Toast.LENGTH_SHORT).show()
                                                    }
                                                } catch (t: Throwable) { // Error durante la digitacion

                                                    val mensajeError = when (t) {
                                                        is com.digitarra.digitacion.AcordeLongitudImposibleException ->
                                                            "No se pudo digitar: La partitura contiene un acorde imposible de ejecutar en guitarra."
                                                        is com.digitarra.digitacion.NotaDesconocidaException ->
                                                            "No se pudo digitar: la partitura contiene una nota imposible de ejecutar: ${t.notaInalcanzable}"
                                                        else ->
                                                            "No se pudo digitar: ${t.localizedMessage ?: "Error desconocido"}"
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(this@MainActivity, mensajeError, Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        },
                                        onEditarPartitura = { partitura ->
                                            Toast.makeText(context, "Cargando datos de ${partitura.nombre_partitura}...", Toast.LENGTH_SHORT).show()

                                            // 1. Asignamos la partitura seleccionada al estado
                                            partituraAEditar = partitura

                                            // 2. Cargamos las notas en segundo plano para no congelar la UI
                                            lifecycleScope.launch(Dispatchers.IO) {
                                                try {
                                                    val digitaciones = biblioteca?.obtenerDigitacionesPartitura(partitura.nombre_partitura) ?: emptyList()

                                                    withContext(Dispatchers.Main) {
                                                        // 3. Guardamos las digitaciones cargadas
                                                        listaDigitacionesAEditar = digitaciones

                                                        // 4. Cambiamos el estado para navegar a la pantalla 3 (Edición)
                                                        pantallaActual = 3
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error al abrir edición: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                            }
                                        },
                                        onEliminarPartitura = { partitura ->
                                            // 1. Elimina archivos internos, HashMap y entradas del JSON
                                            biblioteca?.eliminaPartitura(partitura.nombre_partitura)

                                            // 2. Persiste los cambios en el archivo Partituras.json en disco
//                      biblioteca                      biblioteca?.guardarCambiosEnJson(context)

                                            // 3. Si estábamos dentro de una colección, refrescamos su referencia en memoria
                                            coleccionActiva?.quitarPartitura(partitura.nombre_partitura)
                                            coleccionActiva?.let { col ->
                                                coleccionActiva = biblioteca?.getColeccion(col.nombre)
                                            }

                                            // 4. Forzamos el redibujado de Compose
                                            refrescoKey++

                                            Toast.makeText(
                                                context,
                                                "Partitura \"${partitura.nombre_partitura}\" eliminada",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }
                            2 -> PantallaOpcionesAgregar(
                                onVolverClick = { pantallaActual = 1 },
                                onOpcion1Click = { /* Añadir por audio */ },
                                onOpcion2Click = { nombre: String, uri: Uri?, context: Context ->
//                                    // Tipos MIME filtrados para archivos XML y MIDI
//                                    val mimeTypes = arrayOf(
//                                        "text/xml",
//                                        "application/xml",
//                                        "audio/midi",
//                                        "audio/x-midi"
//                                    )
//
//                                    filePickerLauncher.launch(mimeTypes)
                                    uri?.let { uri ->
                                        try {
                                            //val nombreOriginal = obtenerNombreArchivo(it)
                                            //val extension = nombreOriginal.substringAfterLast(".").lowercase()
                                            Toast.makeText(
                                                context,
                                                "Creando partitura, por favor espera...",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            val creadorPartituras = CreadorPartituras(context)

                                            lifecycleScope.launch {

                                                // Le pasas la URI directamente sin importar si es XML o MIDI
                                                val resultado = creadorPartituras.procesarYGuardarPartitura(uri, biblioteca, nombre)



                                                resultado.fold(
                                                    onSuccess = {
                                                        val temp = biblioteca
                                                        biblioteca = null
                                                        biblioteca = temp
                                                        refrescoKey++
                                                        pantallaActual = 1
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Partitura añadida con éxito",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    },
                                                    onFailure = { error ->
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "Error: ${error.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                )
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(
                                                context,
                                                "Error al procesar el archivo: ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }

                                }
                            )
                            3 -> {
                                val partituraActual = partituraAEditar

                                if (partituraActual != null) {
                                    PantallaEditarPartitura(
                                        partitura = partituraActual,
                                        listaDigitaciones = listaDigitacionesAEditar,
                                        onVolver = { pantallaActual = 1 },
                                        onGuardarCambios = { nuevasDigitaciones ->
                                            Toast.makeText(context, "Guardando digitación...", Toast.LENGTH_SHORT).show()

                                            lifecycleScope.launch(Dispatchers.IO) {
                                                try {
                                                    // 1. Guarda los cambios en el archivo MusicXML a través del script de Python
                                                    biblioteca?.actualizarDigitacionesMusicXML(
                                                        partituraActual.nombre_partitura,
                                                        nuevasDigitaciones
                                                    )

                                                    // 2. Si tu objeto partituraActual o tu ViewModel guarda las digitaciones,
                                                    // actualiza la variable de estado local aquí.
                                                    // Si las lees directamente del archivo XML al abrir la pantalla,
                                                    // no necesitas asignar 'partituraActual.listaDigitaciones'.

                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(this@MainActivity, "Guardado con éxito", Toast.LENGTH_SHORT).show()
                                                        // Cambiar el valor del refresco fuerza a Jetpack Compose a releer los datos actualizados del XML/BD
                                                        refrescoKey++
                                                        pantallaActual = 1
                                                    }
                                                } catch (e: Exception) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                                                    }
                                                }
                                            }
                                        }
                                    )
                                } else {
                                    pantallaActual = 1
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Se ejecuta al MINIMIZAR la app (segundo plano)
    override fun onStop() {
        super.onStop()
        cerrarRecursos()
    }

    // Se ejecuta al CERRAR por completo la app
    override fun onDestroy() {
        super.onDestroy()
        cerrarRecursos()
    }

    private fun cerrarRecursos() {
        try {
            bibliotecaInstancia?.cierraBiblioteca()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

// 0. Inicio
@Composable
fun PantallaInicio(onComenzarClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de Digitarra",
            modifier = Modifier.size(256.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Digitarra",
            style = MaterialTheme.typography.headlineLarge,
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onComenzarClick,
            modifier = Modifier
                .width(280.dp)
                .height(64.dp)
        ) {
            Text(
                text = "Comenzar",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 24.sp
            )
        }
    }
}

// 1. Biblioteca de partituras
@Composable
fun PantallaBiblioteca(
    biblioteca: BibliotecaPartituras?,
    coleccionSeleccionada: Coleccion?,
    onSeleccionarColeccion: (Coleccion?) -> Unit,
    onAgregarClick: () -> Unit,
    onCrearColeccion: (String) -> Unit,
    onAgregarPartiturasAColeccion: (List<Partitura>) -> Unit,
    onQuitarPartiturasDeColeccion: (List<Partitura>) -> Unit, // Nueva lambda
    onBorrarColeccion: (Coleccion) -> Unit,
    onVisualizarPartitura: (Partitura) -> Unit = {},
    onDigitarPartitura: (Partitura) -> Unit = {},
    onEditarPartitura: (Partitura) -> Unit = {},
    onEliminarPartitura: (Partitura) -> Unit = {}
) {
    val colecciones = if (coleccionSeleccionada == null) biblioteca?.allColecciones ?: emptyList() else emptyList()
    val partiturasAMostrar = if (coleccionSeleccionada != null) {
        coleccionSeleccionada.allPartituras
    } else {
        biblioteca?.partiturasSinColeccion ?: emptyList()
    }

    var menuFabExpandido by remember { mutableStateOf(false) }
    var mostrarDialogoColeccion by remember { mutableStateOf(false) }
    var mostrarDialogoAgregarPartituras by remember { mutableStateOf(false) }
    var mostrarDialogoQuitarPartituras by remember { mutableStateOf(false) }

    // Diálogos
    if (mostrarDialogoColeccion) {
        DialogoNuevaColeccion(
            onDismiss = { mostrarDialogoColeccion = false },
            onConfirmar = { nombre ->
                onCrearColeccion(nombre)
                mostrarDialogoColeccion = false
            }
        )
    }




    if (mostrarDialogoAgregarPartituras && biblioteca != null) {
        DialogoAgregarPartiturasAColeccion(
            partiturasSueltas = biblioteca.getPartiturasSinColeccion(),
            onDismiss = { mostrarDialogoAgregarPartituras = false },
            onConfirmar = { seleccionadas ->
                onAgregarPartiturasAColeccion(seleccionadas)
                mostrarDialogoAgregarPartituras = false
            }
        )
    }

    if (mostrarDialogoQuitarPartituras && coleccionSeleccionada != null) {
        DialogoQuitarPartiturasDeColeccion(
            partiturasEnColeccion = coleccionSeleccionada.allPartituras,
            onDismiss = { mostrarDialogoQuitarPartituras = false },
            onConfirmar = { seleccionadas ->
                onQuitarPartiturasDeColeccion(seleccionadas)
                mostrarDialogoQuitarPartituras = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (coleccionSeleccionada == null) {
                Box {
                    FloatingActionButton(onClick = { menuFabExpandido = true }) {
                        Text(text = "+", style = MaterialTheme.typography.headlineMedium)
                    }

                    DropdownMenu(
                        expanded = menuFabExpandido,
                        onDismissRequest = { menuFabExpandido = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Añadir partitura") },
                            onClick = {
                                menuFabExpandido = false
                                onAgregarClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Nueva colección") },
                            onClick = {
                                menuFabExpandido = false
                                mostrarDialogoColeccion = true
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (coleccionSeleccionada != null) {
                IconButton(
                    onClick = { onSeleccionarColeccion(null) },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.volver),
                        contentDescription = "Volver a la biblioteca principal",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                text = if (coleccionSeleccionada != null) "Colección" else "Mi biblioteca",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(top = if (coleccionSeleccionada == null) 16.dp else 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = coleccionSeleccionada?.nombre ?: "Biblioteca de Partituras",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                // Botones para gestionar partituras dentro de la colección
                if (coleccionSeleccionada != null) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { mostrarDialogoQuitarPartituras = true }) {
                            Text("Sacar")
                        }
                        Button(onClick = { mostrarDialogoAgregarPartituras = true }) {
                            Text("+ Añadir")
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(colecciones, key = { it.nombre }) { coleccion ->
                    ItemColeccion(
                        coleccion = coleccion,
                        onAbrir = { onSeleccionarColeccion(coleccion) },
                        onBorrar = { onBorrarColeccion(coleccion) }
                    )
                }

                items(partiturasAMostrar, key = { it.nombre_partitura }) { partitura ->
                    var menuExpandido by remember { mutableStateOf(false) }

                    Box {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clickable { menuExpandido = true },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                // Contenido central (Icono y Nombre)
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.partitura),
                                        contentDescription = "Icono Partitura",
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = partitura.nombre_partitura,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // Etiqueta abajo a la izquierda
                                Text(
                                    text = if (partitura.isDigitada) "Digitada: Sí" else "Digitada: No",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (partitura.isDigitada)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    modifier = Modifier.align(Alignment.BottomStart)
                                )
                            }
                        }

                        // Menú con las opciones al clicar la tarjeta
                        DropdownMenu(
                            expanded = menuExpandido,
                            onDismissRequest = { menuExpandido = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Visualizar") },
                                onClick = {
                                    menuExpandido = false
                                    onVisualizarPartitura(partitura)
                                }
                            )

                            // La opción "Digitar" solo aparece si NO está digitada
                            if (!partitura.isDigitada) {
                                DropdownMenuItem(
                                    text = { Text("Digitar") },
                                    onClick = {
                                        menuExpandido = false
                                        onDigitarPartitura(partitura)
                                    }
                                )
                            }

                            DropdownMenuItem(
                                text = { Text("Editar") },
                                onClick = {
                                    menuExpandido = false
                                    onEditarPartitura(partitura)
                                }
                            )

                            DropdownMenuItem(
                                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    menuExpandido = false
                                    onEliminarPartitura(partitura)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente individual para la Colección con su menú desplegable
@Composable
fun ItemColeccion(
    coleccion: Coleccion,
    onAbrir: () -> Unit,
    onBorrar: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { menuExpandido = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.agrupacion),
                    contentDescription = "Icono Colección",
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = coleccion.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        DropdownMenu(
            expanded = menuExpandido,
            onDismissRequest = { menuExpandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Abrir") },
                onClick = {
                    menuExpandido = false
                    onAbrir()
                }
            )
            DropdownMenuItem(
                text = { Text("Borrar", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    menuExpandido = false
                    onBorrar()
                }
            )
        }
    }
}

// Componente individual para la Partitura con menú de opciones
@Composable
fun ItemPartitura(
    partitura: Partitura,
    onVisualizar: () -> Unit,
    onDigitar: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    var menuExpandido by remember { mutableStateOf(false) }

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clickable { menuExpandido = true },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Contenido central (Icono y Nombre)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.partitura),
                        contentDescription = "Icono Partitura",
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = partitura.nombre_partitura,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }

                // Etiqueta abajo a la izquierda
                Text(
                    text = if (partitura.isDigitada) "Digitada: Sí" else "Digitada: No",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (partitura.isDigitada)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }

        // Menú emergente de opciones al hacer clic
        DropdownMenu(
            expanded = menuExpandido,
            onDismissRequest = { menuExpandido = false }
        ) {
            DropdownMenuItem(
                text = { Text("Visualizar") },
                onClick = {
                    menuExpandido = false
                    onVisualizar()
                }
            )

            // Solo mostrar la opción "Digitar" si no ha sido digitada previamente
            if (!partitura.isDigitada) {
                DropdownMenuItem(
                    text = { Text("Digitar") },
                    onClick = {
                        menuExpandido = false
                        onDigitar()
                    }
                )
            }

            DropdownMenuItem(
                text = { Text("Editar") },
                onClick = {
                    menuExpandido = false
                    onEditar()
                }
            )

            DropdownMenuItem(
                text = { Text("Eliminar", color = MaterialTheme.colorScheme.error) },
                onClick = {
                    menuExpandido = false
                    onEliminar()
                }
            )
        }
    }
}

// 2. Agregar nueva partitura
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaOpcionesAgregar(
    onVolverClick: () -> Unit,
    onOpcion1Click: () -> Unit,
    onOpcion2Click: (String, Uri?, Context) -> Unit
) {
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }
    var nombreInput by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if(mostrarDialogo) {
        DialogoNuevaPartitura(
            onDismiss = { mostrarDialogo = false },
            onConfirmar = { nombre ->
                nombreInput = nombre
                mostrarDialogo = false
                onOpcion2Click(nombre, uriTemporal, context)
            }
        )
    }

    // Launcher de archivos
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            uriTemporal = it
            mostrarDialogo = true // Muestra el diálogo al elegir archivo
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onVolverClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.volver),
                            contentDescription = "Volver a la biblioteca",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Añadir partitura",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Añadir partitura a partir de un audio de guitarra o de un archivo xml / midi del dispositivo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onOpcion1Click,
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.audio),
                            contentDescription = "Audio",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Audio", fontSize = 16.sp)
                    }
                }

                Button(
                    //onClick = onOpcion2Click,
                    onClick = {
                        val mimeTypes = arrayOf(
                            "text/xml",
                            "application/xml",
                            "audio/midi",
                            "audio/x-midi"
                        )
                        filePickerLauncher.launch(mimeTypes)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.xmlmidi),
                            contentDescription = "XML / MIDI",
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "XML / MIDI", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DialogoNuevaColeccion(
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var nombreColeccion by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Colección") },
        text = {
            OutlinedTextField(
                value = nombreColeccion,
                onValueChange = { nombreColeccion = it },
                label = { Text("Nombre de la colección") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombreColeccion.isNotBlank()) {
                        onConfirmar(nombreColeccion.trim())
                    }
                },
                enabled = nombreColeccion.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoNuevaPartitura(
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var nombrePartitura by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Partitura") },
        text = {
            OutlinedTextField(
                value = nombrePartitura,
                onValueChange = { nombrePartitura = it },
                label = { Text("Nombre de la nueva partitura") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nombrePartitura.isNotBlank()) {
                        onConfirmar(nombrePartitura.trim())
                    }
                },
                enabled = nombrePartitura.isNotBlank()
            ) {
                Text("Crear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoAgregarPartiturasAColeccion(
    partiturasSueltas: List<Partitura>,
    onDismiss: () -> Unit,
    onConfirmar: (List<Partitura>) -> Unit
) {
    val seleccionadas = remember { mutableStateListOf<Partitura>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir partituras") },
        text = {
            if (partiturasSueltas.isEmpty()) {
                Text("No hay partituras sueltas disponibles.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(partiturasSueltas) { partitura ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (seleccionadas.contains(partitura)) {
                                        seleccionadas.remove(partitura)
                                    } else {
                                        seleccionadas.add(partitura)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = seleccionadas.contains(partitura),
                                onCheckedChange = { checked ->
                                    if (checked) seleccionadas.add(partitura)
                                    else seleccionadas.remove(partitura)
                                }
                            )
                            Text(
                                text = partitura.nombre_partitura,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(seleccionadas.toList()) },
                enabled = seleccionadas.isNotEmpty()
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogoQuitarPartiturasDeColeccion(
    partiturasEnColeccion: List<Partitura>,
    onDismiss: () -> Unit,
    onConfirmar: (List<Partitura>) -> Unit
) {
    val seleccionadas = remember { mutableStateListOf<Partitura>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sacar partituras") },
        text = {
            if (partiturasEnColeccion.isEmpty()) {
                Text("Esta colección no tiene partituras.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(partiturasEnColeccion) { partitura ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (seleccionadas.contains(partitura)) {
                                        seleccionadas.remove(partitura)
                                    } else {
                                        seleccionadas.add(partitura)
                                    }
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = seleccionadas.contains(partitura),
                                onCheckedChange = { checked ->
                                    if (checked) seleccionadas.add(partitura)
                                    else seleccionadas.remove(partitura)
                                }
                            )
                            Text(
                                text = partitura.nombre_partitura,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirmar(seleccionadas.toList()) },
                enabled = seleccionadas.isNotEmpty()
            ) {
                Text("Sacar", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}










// Editar partitura

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaEditarPartitura(
    partitura: Partitura,
    listaDigitaciones: List<DigitacionNota>,
    onVolver: () -> Unit,
    onGuardarCambios: (List<DigitacionNota>) -> Unit
) {
    val estadoDigitaciones =
        remember { mutableStateListOf(*listaDigitaciones.toTypedArray()) }

    val estaDigitada = partitura.isDigitada

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar: ${partitura.nombre_partitura}") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            painterResource(id = R.drawable.volver),
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = { onGuardarCambios(estadoDigitaciones) },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            if (!estaDigitada) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Partitura no digitada: Puedes modificar las notas (alturas), pero la edición de digitación permanecerá bloqueada hasta que la digites.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                itemsIndexed(estadoDigitaciones) { index, item ->

                    var mostrarDialogoNota by remember {
                        mutableStateOf(false)
                    }

                    /*
                     * Separamos las notas.
                     *
                     * Ejemplo:
                     * "do2" -> ["do2"]
                     * "do2,mi2,sol2" -> ["do2", "mi2", "sol2"]
                     */
                    val notas = item.nombreNota
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {

                            // -------------------------
                            // CABECERA
                            // -------------------------

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = "Compás ${item.compas}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                OutlinedButton(
                                    onClick = {
                                        mostrarDialogoNota = true
                                    }
                                ) {
                                    Text(
                                        text = "Nota: ${item.nombreNota}",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // -------------------------
                            // DIGITACIÓN
                            // -------------------------

                            notas.forEachIndexed { numeroNota, nota ->

                                if (notas.size > 1) {
                                    Text(
                                        text = "Nota ${numeroNota + 1}: $nota",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            bottom = 4.dp
                                        )
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {

                                    SelectorDropdown(
                                        etiqueta = "D.Izq",
                                        valorActual = item.dedoIzquierdo,
                                        opciones = listOf(
                                            "",
                                            "0",
                                            "1",
                                            "2",
                                            "3",
                                            "4"
                                        ),
                                        enabled = estaDigitada,
                                        modifier = Modifier.weight(1f),
                                        onSeleccion = { nuevoDedo ->

                                            estadoDigitaciones[index] =
                                                item.copy(
                                                    dedoIzquierdo = nuevoDedo
                                                )
                                        }
                                    )

                                    SelectorDropdown(
                                        etiqueta = "Traste",
                                        valorActual = item.traste,
                                        opciones = listOf("") +
                                                (0..19).map { it.toString() },
                                        enabled = estaDigitada,
                                        modifier = Modifier.weight(1f),
                                        onSeleccion = { nuevoTraste ->

                                            estadoDigitaciones[index] =
                                                item.copy(
                                                    traste = nuevoTraste
                                                )
                                        }
                                    )

                                    SelectorDropdown(
                                        etiqueta = "Cuerda",
                                        valorActual = item.cuerda,
                                        opciones = listOf(
                                            "",
                                            "1",
                                            "2",
                                            "3",
                                            "4",
                                            "5",
                                            "6"
                                        ),
                                        enabled = estaDigitada,
                                        modifier = Modifier.weight(1f),
                                        onSeleccion = { nuevaCuerda ->

                                            estadoDigitaciones[index] =
                                                item.copy(
                                                    cuerda = nuevaCuerda
                                                )
                                        }
                                    )

                                    SelectorDropdown(
                                        etiqueta = "D.Der",
                                        valorActual = item.manoDerecha,
                                        opciones = listOf(
                                            "",
                                            "p",
                                            "i",
                                            "m",
                                            "a",
                                            "c"
                                        ),
                                        enabled = estaDigitada,
                                        modifier = Modifier.weight(1f),
                                        onSeleccion = { nuevoDedoDer ->

                                            estadoDigitaciones[index] =
                                                item.copy(
                                                    manoDerecha = nuevoDedoDer
                                                )
                                        }
                                    )
                                }

                                if (numeroNota < notas.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(
                                            vertical = 10.dp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // -------------------------
                    // DIÁLOGO PARA CAMBIAR NOTA
                    // -------------------------

                    if (mostrarDialogoNota) {

                        var textoNota by remember {
                            mutableStateOf(item.nombreNota)
                        }

                        var mensajeError by remember {
                            mutableStateOf<String?>(null)
                        }

                        val numeroNotasEsperadas =
                            notas.size

                        AlertDialog(
                            onDismissRequest = {
                                mostrarDialogoNota = false
                            },

                            title = {
                                Text(
                                    if (numeroNotasEsperadas > 1)
                                        "Modificar Acorde"
                                    else
                                        "Modificar Nota"
                                )
                            },

                            text = {

                                OutlinedTextField(
                                    value = textoNota,

                                    onValueChange = {
                                        textoNota = it
                                        mensajeError = null
                                    },

                                    label = {
                                        Text("Alturas de la(s) nota(s)")
                                    },

                                    isError = mensajeError != null,

                                    supportingText = {

                                        if (mensajeError != null) {
                                            Text(
                                                text = mensajeError!!,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        } else {
                                            Text(
                                                if (numeroNotasEsperadas > 1)
                                                    "Introduce $numeroNotasEsperadas notas separadas por comas (Ej: do4,mi4,sol4)"
                                                else
                                                    "Ejemplo: do3, fa#3, mib2"
                                            )
                                        }
                                    },

                                    singleLine = true
                                )
                            },

                            confirmButton = {

                                Button(
                                    onClick = {

                                        val textoLimpio =
                                            textoNota
                                                .replace(" ", "")
                                                .lowercase()

                                        val listaNotasIngresadas =
                                            textoLimpio.split(",")

                                        val regexNotaIndividual =
                                            Regex(
                                                "(?i)^(do|re|mi|fa|sol|la|si)[#b♯♭]?[0-9]?$"
                                            )

                                        if (
                                            listaNotasIngresadas.size !=
                                            numeroNotasEsperadas
                                        ) {

                                            mensajeError =
                                                "Debes introducir exactamente $numeroNotasEsperadas notas separadas por comas."

                                        } else if (
                                            !listaNotasIngresadas.all {
                                                regexNotaIndividual.matches(it)
                                            }
                                        ) {

                                            mensajeError =
                                                "Una o más notas tienen un formato inválido (Ej: do4,mi4,sol4)."

                                        } else {

                                            estadoDigitaciones[index] =
                                                item.copy(
                                                    nombreNota = textoLimpio
                                                )

                                            mostrarDialogoNota = false
                                        }
                                    }
                                ) {
                                    Text("Aceptar")
                                }
                            },

                            dismissButton = {

                                TextButton(
                                    onClick = {
                                        mostrarDialogoNota = false
                                    }
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// SelectorDropdown actualizado para recibir el parámetro `enabled`
@Composable
fun SelectorDropdown(
    etiqueta: String,
    valorActual: String,
    opciones: List<String>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onSeleccion: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedButton(
            onClick = { expandido = true },
            enabled = enabled,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (valorActual.isEmpty()) etiqueta else "$etiqueta:$valorActual",
                style = MaterialTheme.typography.bodySmall
            )
        }
        DropdownMenu(
            expanded = expandido && enabled,
            onDismissRequest = { expandido = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(if (opcion.isEmpty()) "Ninguno" else opcion) },
                    onClick = {
                        onSeleccion(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}


