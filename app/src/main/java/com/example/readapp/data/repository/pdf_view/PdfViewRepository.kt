package com.example.readapp.data.repository.pdf_view

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class PdfViewRepository (private val database: FirebaseDatabase, private val storage: FirebaseStorage) {

    fun getPdfDetails(bookId: String, onSuccess: (String?) -> Unit, onFailure: (DatabaseError) -> Unit) {
        val ref = database.getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value as String?
                    onSuccess(pdfUrl)
                }
                override fun onCancelled(error: DatabaseError) {
                    onFailure(error)
                }
            })
    }

    private companion object{
        const val MAX_BYTES_PDF: Long = 5000000000
    }
    fun getPdfBytes(pdfUrl: String, onSuccess: (ByteArray) -> Unit, onFailure: (Exception) -> Unit) {
        val reference = storage.getReferenceFromUrl(pdfUrl)
        reference.getBytes(MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                onSuccess(bytes)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}