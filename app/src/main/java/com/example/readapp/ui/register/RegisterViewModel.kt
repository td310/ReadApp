package com.example.readapp.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readapp.data.repository.login_register.UserRepository
import kotlinx.coroutines.launch


class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationState = MutableLiveData<Boolean>()
    val registrationState: LiveData<Boolean> get() = _registrationState

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                userRepository.signUp(name, email, password)
                _registrationState.value = true
            } catch (e: Exception) {
                _registrationState.value = false
            }
        }
    }
}

