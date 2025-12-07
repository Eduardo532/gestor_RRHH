package com.example.gestor_empleados.data.repository

import com.example.gestor_empleados.data.model.LeaveRequest
import com.example.gestor_empleados.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class LeaveRepository {

    private val db = FirebaseFirestore.getInstance()
    private val leavesCollection = db.collection(Constants.COLLECTION_LEAVES)
    private val authRepo = AuthRepository()

    suspend fun requestLeave(reason: String): Result<Unit> {
        return try {
            val userId = authRepo.getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val userEmail = authRepo.getCurrentUserEmail() ?: "SIN_RUT"
            val rut = userEmail.split("@")[0]

            val newDocRef = leavesCollection.document()

            val request = LeaveRequest(
                id = newDocRef.id,
                userId = userId,
                rut = rut,
                reason = reason,
                status = "Pendiente"
            )

            newDocRef.set(request).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserLeaves(): Result<List<LeaveRequest>> {
        return try {
            val userId = authRepo.getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = leavesCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val leaves = snapshot.toObjects(LeaveRequest::class.java)
            Result.success(leaves)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}