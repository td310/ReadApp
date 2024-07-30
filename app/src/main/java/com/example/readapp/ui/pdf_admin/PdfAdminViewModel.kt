package com.example.readapp.ui.pdf_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.data.repository.pdf_admin.PdfAdminRepository

class PdfAdminViewModel(private val pdfRepository: PdfAdminRepository) : ViewModel() {
    private val _pdfList = MutableLiveData<ArrayList<ModelPdf>>()
    val pdfList: LiveData<ArrayList<ModelPdf>> get() = _pdfList

    fun loadPdfList(categoryId: String) {
        pdfRepository.getPdfsByCategory(categoryId) { pdfArrayList ->
            _pdfList.value = pdfArrayList
        }
    }
}
