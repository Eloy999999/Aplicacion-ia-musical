package com.digitarra.gestion_partituras

data class DigitacionNota(
    val idNota: String,            //
    val nombreNota: String,        //
    val compas: Int,               // Número de compas
    var dedoIzquierdo: String,     // 0 a 4
    var traste: String,            // 0 a numero de trastes maximos en romanos
    var cuerda: String,            // 1,2,3,4,5,6 en circulo
    var manoDerecha: String        // p, i, c, a
)