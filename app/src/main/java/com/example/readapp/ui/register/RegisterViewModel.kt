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
        userRepository.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val user = mapOf(
                    "uid" to uid,
                    "email" to email,
                    "name" to name,
                    "profileImage" to "",
                    "userType" to "user",
                    "timestamp" to System.currentTimeMillis()
                )
                uid?.let {
                    userRepository.updateUser(it, user)
                        .addOnSuccessListener {
                            _registrationState.value = true
                        }
                        .addOnFailureListener { e ->
                            _errorMessage.value = e.message
                        }
                }
            }
            .addOnFailureListener { e ->
                _errorMessage.value = e.message
            }
    }
}
