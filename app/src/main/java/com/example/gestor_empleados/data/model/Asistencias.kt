package com.example.gestor_empleados.data.model

import com.google.firebase.Timestamp

data class Asistencia(
    val userId: String = "",
    val rut: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val latitud: Double = 0.0,
    val longitud: Double = 0.0
)