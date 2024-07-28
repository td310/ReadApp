package com.example.readapp.data.repository.pdf_admin_edit

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfEditRepository(private val database: FirebaseDatabase) {

    fun loadCategories(
        onSuccess: (List<Pair<String, String>>) -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        val ref = database.getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.map {
                    Pair(it.child("id").value.toString(), it.child("category").value.toString())
                }
                onSuccess(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }

    fun loadBookInfo(
        bookId: String,
        onSuccess: (String, String, String) -> Unit,
        onFailure: (DatabaseError) -> Unit
    ) {
        val ref = database.getReference("Books").child(bookId)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoryId = snapshot.child("categoryId").value.toString()
                val description = snapshot.child("description").value.toString()
                val title = snapshot.child("title").value.toString()
                onSuccess(categoryId, description, title)
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure(error)
            }
        })
    }

    fun updatePdf(
        bookId: String,
        title: String,
        description: String,
        categoryId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["categoryId"] = categoryId

        val ref = database.getReference("Books").child(bookId)
        ref.updateChildren(hashMap).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { e ->
            onFailure(e)
        }
    }
}