package com.example.readapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.login_register.UserRepository


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    fun register(name: String, email: String, password: String) {
        userRepository.signUp(name, email, password)
            .addOnSuccessListener {
                _registrationState.value = true
            }
    }
}

