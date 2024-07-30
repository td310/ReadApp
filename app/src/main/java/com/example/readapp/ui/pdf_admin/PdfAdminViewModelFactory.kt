package com.example.readapp.ui.pdf_admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readapp.data.repository.pdf_admin.PdfAdminRepository

class PdfAdminViewModelFactory(private val repository: PdfAdminRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfAdminViewModel::class.java)) {
            return PdfAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
