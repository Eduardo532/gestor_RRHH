package com.example.gestor_empleados.data.repository

import com.example.gestor_empleados.data.model.WorkplaceConfig
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ConfigRepository {

    private val db = FirebaseFirestore.getInstance()
    private val configRef = db.collection("configuracion").document("ubicacion_trabajo")

    suspend fun getWorkplaceConfig(): Result<WorkplaceConfig> {
        return try {
            val snapshot = configRef.get().await()
            val config = snapshot.toObject(WorkplaceConfig::class.java)

            if (config != null) {
                Result.success(config)
            } else {
                Result.failure(Exception("No se encontró la configuración en Firebase"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}