package com.example.readapp.ui.profile_edit

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelUser
import com.example.readapp.data.repository.profile_edit.ProfileEditRepository

class ProfileEditViewModel(private val repository: ProfileEditRepository) : ViewModel() {

    private val _userInfo = MutableLiveData<ModelUser?>()
    val userInfo: LiveData<ModelUser?> get() = _userInfo

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> get() = _updateStatus

    private val _uploadImageUrl = MutableLiveData<String?>()
    val uploadImageUrl: LiveData<String?> get() = _uploadImageUrl

    fun loadUserInfo() {
        repository.getUserInfo { user ->
            _userInfo.value = user
        }
    }

    fun updateProfile(name: String, uploadedImageUrl: String?) {
        repository.updateProfile(name, uploadedImageUrl) { success ->
            _updateStatus.value = success
        }
    }

    fun uploadImage(imageUri: Uri) {
        repository.uploadImage(imageUri) { imageUrl ->
            _uploadImageUrl.value = imageUrl
        }
    }
}