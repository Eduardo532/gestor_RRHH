package com.example.gestor_empleados.data.model

import com.google.firebase.Timestamp

data class Attendance(
    val userId: String = "",
    val rut: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)