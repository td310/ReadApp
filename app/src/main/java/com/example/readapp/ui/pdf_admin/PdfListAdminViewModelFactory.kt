package com.example.readapp.ui.pdf_admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.readapp.data.repository.pdf_admin.PdfListRepository

class PdfListAdminViewModelFactory(private val repository: PdfListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfListAdminViewModel::class.java)) {
            return PdfListAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
