package com.example.gestor_empleados.data.repository

import com.example.gestor_empleados.data.model.Asistencia
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query // <-- AÑADE ESTA IMPORTACIÓN
import kotlinx.coroutines.tasks.await

class AsistenciaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val asistenciaCollection = db.collection("asistencia")

    private val authRepo = AuthRepository()

    suspend fun registrarAsistencia(asistencia: Asistencia): Result<Unit> {
        return try {
            asistenciaCollection.add(asistencia).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistorialDeAsistencia(): Result<List<Asistencia>> {
        return try {
            val userId = authRepo.getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val snapshot = asistenciaCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val historial = snapshot.toObjects(Asistencia::class.java)
            Result.success(historial)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}