package com.example.readapp.ui.dashboard_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.data.repository.pdf_user.BookRepository

class DashboardUserViewModel(private val repository: BookRepository) : ViewModel() {

    private val _categories = MutableLiveData<List<ModelCategory>>()
    val categories: LiveData<List<ModelCategory>> get() = _categories

    fun fetchCategoriesWithDefaults() {
        val defaultCategories = listOf(
            ModelCategory("01", "All", 1, ""),
            ModelCategory("02", "Most Viewed", 1, ""),
            ModelCategory("03", "Most Downloaded", 1, "")
        )

        repository.fetchCategories { categories ->
            _categories.value = defaultCategories + categories
        }
    }
}