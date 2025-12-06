package com.example.gestor_empleados.data.model

import com.google.firebase.Timestamp

data class LeaveRequest(
    val id: String = "",
    val userId: String = "",
    val rut: String = "",
    val reason: String = "",
    val status: String = "Pendiente",
    val timestamp: Timestamp = Timestamp.now()
)