package com.example.readapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.login_register.UserRepository
import com.google.firebase.auth.FirebaseAuth


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun register(name: String, email: String, password: String) {
        userRepository.signUp(name, email, password)
            .addOnSuccessListener {
                _registrationState.value = true
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
            }
    }
}

