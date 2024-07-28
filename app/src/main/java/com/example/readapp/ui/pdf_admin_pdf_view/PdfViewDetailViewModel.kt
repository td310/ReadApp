package com.example.readapp.ui.pdf_admin_pdf_view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.pdf_admin_pdf_view.PdfViewRepository

class PdfViewDetailViewModel(private val repository: PdfViewRepository) : ViewModel() {
    private val _pdfBytes = MutableLiveData<ByteArray>()
    val pdfBytes: LiveData<ByteArray> = _pdfBytes

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _pageTitle = MutableLiveData<String>()
    val pageTitle: LiveData<String> = _pageTitle

    fun loadBookDetails(bookId: String) {
        repository.getBookDetails(bookId, { pdfUrl ->
            if (pdfUrl != null) {
                loadBookFromUrl(pdfUrl)
            } else {
                _errorMessage.value = "URL not found"
            }
        }, { error ->
            _errorMessage.value = error.message
        })
    }

    private fun loadBookFromUrl(pdfUrl: String) {
        repository.getPdfBytes(pdfUrl, { bytes ->
            _pdfBytes.value = bytes
        }, { exception ->
            _errorMessage.value = exception.message
        })
    }

    fun updatePageTitle(currentPage: Int, pageCount: Int) {
        _pageTitle.value = "$currentPage/$pageCount"
    }
}