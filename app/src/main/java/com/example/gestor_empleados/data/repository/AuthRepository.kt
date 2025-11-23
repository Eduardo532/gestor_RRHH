package com.example.gestor_empleados.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository{

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, password: String): Result<Unit>{
        return try{
            auth.signInWithEmailAndPassword(email,password).await()
            Result.success(Unit)
        } catch (e : Exception){
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun logout() {
        auth.signOut()
    }









}