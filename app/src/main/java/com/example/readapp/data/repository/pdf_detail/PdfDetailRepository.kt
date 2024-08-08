package com.example.readapp.data.repository.pdf_detail

import com.example.readapp.data.model.ModelComment
import com.example.readapp.data.model.ModelPdf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfDetailRepository(
    private val database: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth,
) {
    fun getBookDetails(bookId: String, callback: (ModelPdf) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val book = snapshot.getValue(ModelPdf::class.java)
                book?.let { callback(it) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun incrementDownloadCount(bookId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var downloadsCount =
                    snapshot.child("downloadsCount").getValue(Long::class.java) ?: 0
                val newDownloadsCount = downloadsCount + 1
                ref.child(bookId).child("downloadsCount").setValue(newDownloadsCount)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun downloadBook(bookUrl: String, callback: (ByteArray?) -> Unit) {
        if (bookUrl.isEmpty()) {
            callback(null)
            return
        }
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes -> callback(bytes) }
            .addOnFailureListener { callback(null) }
    }

    fun checkIsFavorite(bookId: String, onResult: (Boolean) -> Unit) {
        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun addToFavorite(bookId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val timestamp = System.currentTimeMillis()
        val hashMap = HashMap<String, Any>()
        hashMap["bookId"] = bookId
        hashMap["timestamp"] = timestamp

        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .setValue(hashMap)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun removeFromFavorite(bookId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val ref = database.getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .removeValue()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addComment(comment: ModelComment, callback: () -> Unit) {
        val ref =
            database.getReference("Books").child(comment.bookId).child("Comments").child(comment.id)
        ref.setValue(comment).addOnSuccessListener {
            callback()
        }
    }

    fun fetchComments(bookId: String, callback: (List<ModelComment>) -> Unit) {
        val ref = database.getReference("Books").child(bookId).child("Comments")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val commentsList = mutableListOf<ModelComment>()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelComment::class.java)
                    if (model != null) {
                        commentsList.add(model)
                    }
                }
                callback(commentsList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}