package com.example.readapp.ui.pdf

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.data.repository.pdf.PdfRepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class PdfAddViewModel(private val pdfRepository: PdfRepository) : ViewModel() {

    private val _uploadState = MutableLiveData<Boolean>()
    val uploadState: LiveData<Boolean> get() = _uploadState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _categories = MutableLiveData<List<ModelCategory>>()
    val categories: LiveData<List<ModelCategory>> get() = _categories

    internal var uploadedPdfUrl: String? = null
    private var timestamp: Long = 0

    fun uploadPdfToFirebaseStorage(pdfUri: Uri) {
        timestamp = System.currentTimeMillis()
        val filePath = "Books/$timestamp"
        pdfRepository.uploadPdfToFirebaseStorage(pdfUri, filePath)
            .addOnSuccessListener { uri ->
                uploadedPdfUrl = uri.toString()
                _uploadState.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
                _uploadState.value = false
            }
    }

    fun uploadPdfInfoToFirebase(
        title: String,
        description: String,
        categoryId: String
    ) {
        val uid = FirebaseAuth.getInstance().uid ?: return
        uploadedPdfUrl?.let { url ->
            pdfRepository.uploadPdfInfoToFirebase(uid, timestamp, title, description, categoryId, url)
                .addOnSuccessListener {
                    _uploadState.value = true
                }
                .addOnFailureListener { e ->
                    _errorMessage.value = e.message
                    _uploadState.value = false
                }
        }
    }

    fun loadPdfCategories() {
        pdfRepository.getPdfCategories().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = snapshot.children.mapNotNull { it.getValue(ModelCategory::class.java) }
                _categories.value = categories
            }

            override fun onCancelled(error: DatabaseError) {
                _errorMessage.value = error.message
            }
        })
    }
}

