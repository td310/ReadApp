package com.example.readapp.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.data.model.ModelUser
import com.example.readapp.data.repository.profile.ProfileRepository

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {

    private val _userInfo = MutableLiveData<ModelUser?>()
    val userInfo: LiveData<ModelUser?> get() = _userInfo

    val booksArrayList = MutableLiveData<ArrayList<ModelPdf>>()

    fun loadUserInfo() {
        repository.getUserInfo { user ->
            _userInfo.value = user
        }
    }

    fun loadFavoriteBooks() {
        repository.loadFavoriteBooks { books ->
            booksArrayList.postValue(books)
        }
    }
}