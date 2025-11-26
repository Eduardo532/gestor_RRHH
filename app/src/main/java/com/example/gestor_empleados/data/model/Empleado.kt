package com.example.gestor_empleados.data.model

import java.util.Date

data class Empleado (
    val uid: String = "",
    val rut: String = "",
    val nombre: String = "",
    val email: String = "",
    val fechaIngreso: Date = Date(),
    val password: String = "",
    val VacacioneDisp: Int = 0
)