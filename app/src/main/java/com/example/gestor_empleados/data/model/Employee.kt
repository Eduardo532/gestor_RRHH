package com.example.gestor_empleados.data.model

import java.util.Date

data class Employee (
    val uid: String = "",
    val rut: String = "",
    val name: String = "",
    val email: String = "",
    val hireDate: Date = Date(),
    val password: String = "",
    val vacationDays: Int = 0
)