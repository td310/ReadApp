package com.example.readapp.ui.pdf_admin_edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.pdf_admin_edit.PdfEditRepository

class PdfEditViewModel(private val repository: PdfEditRepository) : ViewModel() {
    private val _categories = MutableLiveData<List<Pair<String, String>>>()
    val categories: LiveData<List<Pair<String, String>>> = _categories

    private val _bookInfo = MutableLiveData<Triple<String, String, String>>()
    val bookInfo: LiveData<Triple<String, String, String>> = _bookInfo

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun loadCategories() {
        repository.loadCategories(
            onSuccess = {
                _categories.postValue(it)
            },
            onFailure = {
                _error.postValue(it.message)
            }
        )
    }

    fun loadBookInfo(bookId: String) {
        repository.loadBookInfo(
            bookId,
            onSuccess = { categoryId, description, title ->
                _bookInfo.postValue(Triple(categoryId, description, title))
            },
            onFailure = {
                _error.postValue(it.message)
            }
        )
    }

    fun updatePdf(bookId: String, title: String, description: String, categoryId: String) {
        repository.updatePdf(
            bookId, title, description, categoryId,
            onSuccess = {
                _updateStatus.postValue(true)
            },
            onFailure = { e ->
                _error.postValue(e.message)
                _updateStatus.postValue(false)
            }
        )
    }
}