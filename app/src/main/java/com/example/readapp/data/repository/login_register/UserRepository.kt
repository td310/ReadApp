package com.example.readapp.data.repository.login_register

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DataSnapshot

class UserRepository(private val firebaseAuth: FirebaseAuth, private val firebaseDatabase: FirebaseDatabase) {
    //sign up

    //authentication
    fun createUserWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
    }
    //update to realtime
    fun updateUser(uid: String, user: Map<String, Any?>): Task<Void> {
        return firebaseDatabase.getReference("Users").child(uid).setValue(user)
    }

    //login
    fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, password)
    }

    fun getUserType(uid: String): Task<DataSnapshot> {
        return firebaseDatabase.getReference("Users").child(uid).get()
    }
}
