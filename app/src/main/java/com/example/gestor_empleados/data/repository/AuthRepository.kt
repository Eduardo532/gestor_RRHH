package com.example.gestor_empleados.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository{

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun login(email: String, contrasena: String): Result<Unit>{
        return try{
            auth.signInWithEmailAndPassword(email,contrasena).await()
            Result.success(Unit)
        } catch (e : Exception){
            Result.failure(e)
        }
    }










}