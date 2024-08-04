package com.example.readapp.ui.register

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.databinding.ActivityRegisterBinding
import com.example.readapp.ui.dashboard_user.DashboardUserActivity
import com.example.readapp.ui.login.LoginActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var progressDialog: ProgressDialog
    private val registerViewModel: RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.signUpBtn.setOnClickListener {
            validateData()
        }

        registerViewModel.registrationState.observe(this) { success ->
            progressDialog.dismiss()
            if (success) {
                Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        registerViewModel.errorMessage.observe(this) { error ->
            progressDialog.dismiss()
            Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
        }
    }

    private var name = ""
    private var email = ""
    private var password = ""

    private fun validateData() {
        name = binding.nameEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()
        val confirmPassword = binding.confirmpasswordEt.text.toString()

        when {
            name.isEmpty() -> Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            password.isEmpty() -> Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
            confirmPassword.isEmpty() -> Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show()
            password != confirmPassword -> Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
            else -> {
                progressDialog.setTitle("Creating Account")
                progressDialog.show()
                registerViewModel.register(name, email, password)
            }
        }
    }

}