package com.example.readapp.ui.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.R
import com.example.readapp.databinding.ActivityLoginBinding
import com.example.readapp.ui.dashboard_admin.DashboardAdminActivity
import com.example.readapp.ui.dashboard_user.DashboardUserActivity
import com.example.readapp.ui.register.RegisterActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
        private lateinit var progressDialog: ProgressDialog
    private val loginViewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.noAccountTv.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.loginBtn.setOnClickListener {
            validateData()
        }

        binding.forgotTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        //login success turn off progress
        loginViewModel.loginState.observe(this, Observer { success ->
            if (success) {
                progressDialog.dismiss()
            }
        })

        //check user type
        loginViewModel.userType.observe(this, Observer { userType ->
            progressDialog.dismiss()
            if (userType == "User") {
                startActivity(Intent(this, DashboardUserActivity::class.java))
                finish()
            } else if (userType == "admin") {
                startActivity(Intent(this, DashboardAdminActivity::class.java))
                finish()
            }
        })
    }

    private var email = ""
    private var password = ""
    private fun validateData() {
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString()

        when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
            }
            password.isEmpty() -> {
                Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show()
            }
            else -> {
                progressDialog.setTitle("Logging In")
                progressDialog.show()
                loginViewModel.login(email, password)
            }
        }
    }
}