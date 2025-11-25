package com.example.gestor_empleados.data.repository

import com.example.gestor_empleados.data.model.Attendance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import com.example.gestor_empleados.utils.Constants

class AttendanceRepository {
    private val db = FirebaseFirestore.getInstance()
    private val attendanceCollection = db.collection(Constants.COLECCIONS_ATTENDANCE)

    private val authRepo = AuthRepository()

    suspend fun registerAttendance(attendance: Attendance): Result<Unit> {
        return try {
            attendanceCollection.add(attendance).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAttendanceHistory(): Result<List<Attendance>> {
        return try {
            val userId = authRepo.getCurrentUserId()
            if (userId == null) {
                return Result.failure(Exception("Usuario no autenticado"))
            }

            val snapshot = attendanceCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val history = snapshot.toObjects(Attendance::class.java)
            Result.success(history)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}