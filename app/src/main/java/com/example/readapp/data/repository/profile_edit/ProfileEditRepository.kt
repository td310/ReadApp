package com.example.readapp.data.repository.profile_edit

import android.net.Uri
import com.example.readapp.data.model.ModelUser
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class ProfileEditRepository(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) {

    fun getUserInfo(onResult: (ModelUser?) -> Unit) {
        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = ModelUser()
                    onResult(user)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }

    fun updateProfile(name: String, uploadedImageUrl: String?, onComplete: (Boolean) -> Unit) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["name"] = name
        uploadedImageUrl?.let {
            hashMap["profileImage"] = it
        }

        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val filePathAndName = "ProfileImages/" + firebaseAuth.uid
        val reference = storage.reference.child(filePathAndName)
        reference.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    onComplete(uri.toString())
                }
            }
            .addOnFailureListener {
                onComplete(null)
            }
    }
    fun reauthenticateUser(password: String, onComplete: (Boolean) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user != null && user.email != null) {
            val credential = EmailAuthProvider.getCredential(user.email!!, password)
            user.reauthenticate(credential).addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
        } else {
            onComplete(false)
        }
    }

    fun changePassword(newPassword: String, onComplete: (Boolean) -> Unit) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
        } else {
            onComplete(false)
        }
    }
}