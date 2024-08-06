package com.example.readapp.ui.pdf_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.data.repository.dashboard_user.DashboardUserRepository

class PdfUserViewModel(private val repository: DashboardUserRepository) : ViewModel() {

    private val _books = MutableLiveData<List<ModelPdf>>()
    val books: LiveData<List<ModelPdf>> = _books

    fun fetchAllBooks() {
        repository.getAllBooks {
            _books.postValue(it)
        }
    }

    fun fetchBooksByOrder(orderBy: String) {
        repository.getBooksByOrder(orderBy) {
            _books.postValue(it)
        }
    }

    fun fetchBooksByCategory(categoryId: String) {
        repository.getBooksByCategory(categoryId) {
            _books.postValue(it)
        }
    }
}
