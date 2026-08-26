package com.example.digitarra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // Estado numérico: 0 = inicio, 1 = biblioteca, 2 = opciones agregar partitura
                var pantallaActual by remember { mutableStateOf(0) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (pantallaActual) {
                            0 -> PantallaInicio(onComenzarClick = { pantallaActual = 1 })
                            1 -> PantallaBiblioteca(onAgregarClick = { pantallaActual = 2 })
                            2 -> PantallaOpcionesAgregar(
                                onVolverClick = { pantallaActual = 1},
                                onOpcion1Click = { /* Añadir por audio */ },
                                onOpcion2Click = { /* Añadir por xml/midi */ }
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
fun PantallaBiblioteca(onAgregarClick: () -> Unit) {
    val partituras = listOf("Escala_Mayor.xml", "Arpegiado_Do.xml", "Estudio_No1.xml")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Minitítulo
            Text(
                text = "Mi biblioteca",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            // Título Principal
            Text(
                text = "Biblioteca de Partituras",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(partituras) { partitura ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = partitura,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
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
                            painter = painterResource(id = R.drawable.volver), // Nombre de tu imagen en res/drawable
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
            // Título
            Text(
                text = "Añadir partitura",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Descripción
            Text(
                text = "Añadir partitura a partir de un audio de guitarra o de un archivo xml / midi del dispositivo",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Fila de Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Audio (Izquierda)
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

                // XML / MIDI (Derecha)
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