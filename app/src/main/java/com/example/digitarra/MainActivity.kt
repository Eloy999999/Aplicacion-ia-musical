package com.example.digitarra

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.foundation.lazy.items
import com.example.digitarra.Java.gestion_partituras.BibliotecaPartituras
import com.example.digitarra.Java.gestion_partituras.Coleccion
import com.example.digitarra.Java.gestion_partituras.Partitura
import org.json.JSONObject
import java.io.File

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.digitarra.Java.gestion_partituras.GeneradorPDF
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                var pantallaActual by remember { mutableStateOf(0) }
                var biblioteca by remember { mutableStateOf<BibliotecaPartituras?>(null) }
                var coleccionActiva by remember { mutableStateOf<Coleccion?>(null) }

                // Variable de estado para forzar el redibujado de Compose al modificar la biblioteca
                var refrescoKey by remember { mutableIntStateOf(0) }

                val context = LocalContext.current

                // Registrar el launcher para seleccionar archivos XML o MIDI
                val filePickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    uri?.let {
                        try {
                            //val nombreOriginal = obtenerNombreArchivo(it)
                            //val extension = nombreOriginal.substringAfterLast(".").lowercase()

                            Toast.makeText(
                                context,
                                "Generando PDF, por favor espera...",
                                Toast.LENGTH_SHORT
                            ).show()

                            val creadorPartituras = CreadorPartituras(this)

                            lifecycleScope.launch {
                                // Le pasas la URI directamente sin importar si es XML o MIDI
                                val resultado = creadorPartituras.procesarYGuardarPartitura(uri, biblioteca)

                                resultado.fold(
                                    onSuccess = {
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

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (pantallaActual) {
                            0 -> {
                                PantallaInicio(
                                    onComenzarClick = {
                                        try {
                                            val file = File(context.filesDir, "Partituras.json")

                                            // Lee de la memoria interna si ya fue modificado, o del asset si es la primera vez
                                            val jsonContent = if (file.exists()) {
                                                file.readText()
                                            } else {
                                                context.assets.open("Biblioteca/Partituras.json")
                                                    .bufferedReader()
                                                    .use { it.readText() }
                                            }

                                            val jsonObject = JSONObject(jsonContent)
                                            biblioteca = BibliotecaPartituras(jsonObject)
                                            pantallaActual = 1
                                        } catch (e: Exception) {
                                            e.printStackTrace()
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
                                        onSeleccionarColeccion = { coleccion ->
                                            coleccionActiva = coleccion
                                        },
                                        onAgregarClick = { pantallaActual = 2 },
                                        onCrearColeccion = { nombre ->
                                            biblioteca?.addColeccion(Coleccion(nombre, emptyList()))
                                            biblioteca?.guardarCambiosEnJson(context)
                                            refrescoKey++
                                        },
                                        onAgregarPartiturasAColeccion = { seleccionadas ->
                                            // ... lógica existente ...
                                        },
                                        onQuitarPartiturasDeColeccion = { partiturasAQuitar ->
                                            // ... lógica existente ...
                                        },
                                        onBorrarColeccion = { coleccion ->
                                            biblioteca?.eliminaColeccion(coleccion.nombre)
                                            biblioteca?.guardarCambiosEnJson(context)
                                            if (coleccionActiva == coleccion) coleccionActiva = null
                                            refrescoKey++
                                        },
                                        // --- NUEVAS ACCIONES ---
                                        onVisualizarPartitura = { partitura ->
                                            Toast.makeText(context, "Visualizando: ${partitura.nombre_partitura}", Toast.LENGTH_SHORT).show()
                                            // TODO: Abrir pantalla de visualización del PDF
                                        },
                                        onDigitarPartitura = { partitura ->
                                            Toast.makeText(context, "Digitando: ${partitura.nombre_partitura}", Toast.LENGTH_SHORT).show()
                                            // TODO: Iniciar proceso de digitación
                                        },
                                        onEditarPartitura = { partitura ->
                                            Toast.makeText(context, "Editando: ${partitura.nombre_partitura}", Toast.LENGTH_SHORT).show()
                                            // TODO: Abrir pantalla de edición
                                        },
                                        onEliminarPartitura = { partitura ->
                                            // 1. Elimina archivos internos, HashMap y entradas del JSON
                                            biblioteca?.eliminarPartitura(partitura.nombre_partitura)

                                            // 2. Persiste los cambios en el archivo Partituras.json en disco
                                            biblioteca?.guardarCambiosEnJson(context)

                                            // 3. Si estábamos dentro de una colección, refrescamos su referencia en memoria
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
                                onOpcion2Click = {
                                    // Tipos MIME filtrados para archivos XML y MIDI
                                    val mimeTypes = arrayOf(
                                        "text/xml",
                                        "application/xml",
                                        "audio/midi",
                                        "audio/x-midi"
                                    )
                                    filePickerLauncher.launch(mimeTypes)
                                }
                            )
                        }
                    }
                }
            }
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
    onQuitarPartiturasDeColeccion: (List<Partitura>) -> Unit,
    onBorrarColeccion: (Coleccion) -> Unit,
    onVisualizarPartitura: (Partitura) -> Unit = {},
    onDigitarPartitura: (Partitura) -> Unit = {},
    onEditarPartitura: (Partitura) -> Unit = {},
    onEliminarPartitura: (Partitura) -> Unit = {}
) {
    val colecciones = if (coleccionSeleccionada == null) biblioteca?.allColecciones
        ?: emptyList() else emptyList()
    val partiturasAMostrar = if (coleccionSeleccionada != null) {
        coleccionSeleccionada.partituras
    } else {
        biblioteca?.getPartiturasSueltas() ?: emptyList()
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
            partiturasSueltas = biblioteca.getPartiturasSueltas(),
            onDismiss = { mostrarDialogoAgregarPartituras = false },
            onConfirmar = { seleccionadas ->
                onAgregarPartiturasAColeccion(seleccionadas)
                mostrarDialogoAgregarPartituras = false
            }
        )
    }

    if (mostrarDialogoQuitarPartituras && coleccionSeleccionada != null) {
        DialogoQuitarPartiturasDeColeccion(
            partiturasEnColeccion = coleccionSeleccionada.partituras,
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
    onOpcion2Click: () -> Unit
) {
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
                    onClick = onOpcion2Click,
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