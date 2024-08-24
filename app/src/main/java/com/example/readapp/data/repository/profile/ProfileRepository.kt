package com.example.readapp.data.repository.profile

import com.example.readapp.data.model.ModelPdf
import com.example.readapp.data.model.ModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileRepository(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
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

    fun loadFavoriteBooks(callback: (ArrayList<ModelPdf>) -> Unit) {
        val booksArrayList = ArrayList<ModelPdf>()
        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                booksArrayList.clear()
                for (ds in snapshot.children) {
                    val bookId = ds.child("bookId").value.toString()
                    val modelPdf = ModelPdf()
                    modelPdf.id = bookId
                    booksArrayList.add(modelPdf)
                }
                callback(booksArrayList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}