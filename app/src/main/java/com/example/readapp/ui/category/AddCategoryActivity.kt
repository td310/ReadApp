package com.example.readapp.ui.category

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.databinding.ActivityAddCategoryBinding
import com.example.readapp.ui.dashboard_admin.DashboardAdminActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var progressDialog: ProgressDialog
    private val categoryViewModel: AddCategoryViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        //back btn
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //upload cateogry
        binding.submitBtn.setOnClickListener {
            validateData()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        categoryViewModel.addCategoryState.observe(this, { isSuccess ->
            progressDialog.dismiss()
            if (isSuccess) {
                Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardAdminActivity::class.java))
                finish()
            }
        })
    }

    private var category = ""
    private fun validateData() {
        category = binding.categoryEt.text.toString().trim()

        if (category.isEmpty()) {
            Toast.makeText(this, "Enter your category", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.show()
            categoryViewModel.addCategory(category)
        }
    }
}