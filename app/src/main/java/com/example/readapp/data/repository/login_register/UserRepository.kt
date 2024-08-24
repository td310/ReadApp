package com.example.readapp.data.repository.login_register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.tasks.await

class UserRepository(private val firebaseAuth: FirebaseAuth, private val firebaseDatabase: FirebaseDatabase) {

    //sign up
    suspend fun signUp(name: String, email: String, password: String) {
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID is null")
            val user = mapOf(
                "uid" to uid,
                "email" to email,
                "name" to name,
                "profileImage" to "",
                "userType" to "User",
                "timestamp" to System.currentTimeMillis()
            )
            updateUser(uid, user).await()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun updateUser(uid: String, user: Map<String, Any?>): Task<Void> {
        return firebaseDatabase.getReference("Users").child(uid).setValue(user)
    }

    //login
    fun login(email: String, password: String): Task<DataSnapshot> {
        return firebaseAuth.signInWithEmailAndPassword(email, password).continueWithTask { task ->
            if (task.isSuccessful) {
                val uid = firebaseAuth.currentUser?.uid ?: throw Exception("User ID is null")
                getUserType(uid)
            } else {
                throw task.exception ?: Exception("Unknown error")
            }
        }
    }

    private fun getUserType(uid: String): Task<DataSnapshot> {
        return firebaseDatabase.getReference("Users").child(uid).get()
    }
}

