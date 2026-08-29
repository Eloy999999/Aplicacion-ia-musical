package com.example.digitarra

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.digitarra.Java.gestion_partituras.BibliotecaPartituras
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class PartiturasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CreadorPartituras(application)

    var cargando by mutableStateOf(false)
        private set

    fun procesarArchivo(
        uri: Uri,
        biblioteca: BibliotecaPartituras?,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            cargando = true
            val resultado = repository.procesarYGuardarPartitura(uri, biblioteca)
            cargando = false

            resultado.onSuccess {
                onExito()
            }.onFailure { error ->
                onError(error.message ?: "Error desconocido")
            }
        }
    }
}