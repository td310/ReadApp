package com.example.readapp.ui.dashboard_admin

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.adapter.AdapterDashboardAdmin
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.databinding.ActivityDashboardAdminBinding
import com.example.readapp.ui.category.AddCategoryActivity
import com.example.readapp.ui.login.LoginActivity
import com.example.readapp.ui.pdf.PdfAddActivity
import com.example.readapp.ui.profile.ProfileActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardAdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardAdminBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var adapterDashboardAdmin: AdapterDashboardAdmin

    private val categoryViewModel: DashboardAdminViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //logout
        binding.logoutBtn.setOnClickListener {
            firebaseAuth.signOut()
            checkUser()
        }

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adapterDashboardAdmin.filter.filter(s)
                }
                catch (e:Exception){
                }
            }
            override fun afterTextChanged(s: Editable?) {
            }
        })

        //add category
        binding.addCategoryBtn.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }

        //add pdf
        binding.addPdFab.setOnClickListener {
            startActivity(Intent(this, PdfAddActivity::class.java))
        }

        binding.profileBtn.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        setupObserve()
        categoryViewModel.loadCategories()

    }

    private fun setupObserve(){
        categoryViewModel.categories.observe(this, { categoryList ->
            adapterDashboardAdmin = AdapterDashboardAdmin(this, categoryList as ArrayList<ModelCategory>)
            binding.categoryRv.adapter = adapterDashboardAdmin
        })

        categoryViewModel.errorMessage.observe(this, { errorMessage ->
            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        })
    }

    private fun checkUser() {
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //not login, go back loggin
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
        else{
            val email = firebaseUser.email
            binding.subTitleTv.text = email
        }
    }
}