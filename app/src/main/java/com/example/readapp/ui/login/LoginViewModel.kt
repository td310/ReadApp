package com.example.readapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.login_register.UserRepository

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val _userType = MutableLiveData<String>()
    val userType: LiveData<String> get() = _userType

    fun login(email: String, password: String) {
        userRepository.login(email, password)
            .addOnSuccessListener { snapshot ->
                _userType.value = snapshot.child("userType").value as String
                _loginState.value = true
            }
    }
}

