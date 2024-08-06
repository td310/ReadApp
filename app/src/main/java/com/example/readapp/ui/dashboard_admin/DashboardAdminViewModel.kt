package com.example.readapp.ui.dashboard_admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.data.repository.dashboard_admin.DashboardAdminRepository

class DashboardAdminViewModel(private val repository: DashboardAdminRepository) : ViewModel() {
    private val _categories = MutableLiveData<List<ModelCategory>>()
    val categories: LiveData<List<ModelCategory>> get() = _categories

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadCategories() {
        repository.loadCategories(
            onSuccess = { categoryList ->
                _categories.value = categoryList
            }
        )
    }
}