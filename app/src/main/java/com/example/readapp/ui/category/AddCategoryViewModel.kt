package com.example.readapp.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.category.CategoryRepository
import com.google.firebase.auth.FirebaseAuth

class AddCategoryViewModel(private val categoryRepository: CategoryRepository): ViewModel() {
    private val _addCategoryState = MutableLiveData<Boolean>()
    val addCategoryState: LiveData<Boolean> get() = _addCategoryState
    fun addCategory(category: String) {
        val uid = FirebaseAuth.getInstance().uid
        val timestamp = System.currentTimeMillis()

        if (uid != null) {
            categoryRepository.addCategory(uid, category, timestamp)
                .addOnSuccessListener {
                    _addCategoryState.value = true
                }
        } else {
        }
    }
}