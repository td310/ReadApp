package com.example.readapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.repository.login_register.UserRepository
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginState = MutableLiveData<Boolean>()
    val loginState: LiveData<Boolean> get() = _loginState

    private val _userType = MutableLiveData<String>()
    val userType: LiveData<String> get() = _userType

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, password: String) {
        userRepository.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                //if user id not null then get user type
                uid?.let { id ->
                    userRepository.getUserType(id)
                        .addOnSuccessListener { snapshot ->
                            _userType.value = snapshot.child("userType").value as String
                            _loginState.value = true
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
