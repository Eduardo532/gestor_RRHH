package com.example.gestor_empleados.data.repository

import com.example.gestor_empleados.data.model.Asistencia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AsistenciaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val asistenciaCollection = db.collection("asistencia")

    suspend fun registrarAsistencia(asistencia: Asistencia): Result<Unit> {
        return try {
            asistenciaCollection.add(asistencia).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
