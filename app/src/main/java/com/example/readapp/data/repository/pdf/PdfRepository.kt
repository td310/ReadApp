package com.example.readapp.data.repository.pdf

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class PdfRepository {

    fun uploadPdfToFirebaseStorage(pdfUri: Uri, filePath: String): Task<Uri> {
        val storageReference = FirebaseStorage.getInstance().getReference(filePath)
        return storageReference.putFile(pdfUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                storageReference.downloadUrl
            }
    }

    fun uploadPdfInfoToFirebase(
        uid: String,
        timestamp: Long,
        title: String,
        description: String,
        categoryId: String,
        uploadedPdfUrl: String
    ): Task<Void> {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = uid
        hashMap["id"] = "$timestamp"
        hashMap["title"] = title
        hashMap["description"] = description
        hashMap["categoryId"] = categoryId
        hashMap["url"] = uploadedPdfUrl
        hashMap["timestamp"] = timestamp
        hashMap["viewsCount"] = 0
        hashMap["downloadsCount"] = 0

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        return ref.child("$timestamp").setValue(hashMap)
    }

    fun getPdfCategories(): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("Categories")
    }
}


