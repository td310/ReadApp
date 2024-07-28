package com.example.readapp.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.readapp.R
import com.example.readapp.adapter.AdapterProfile
import com.example.readapp.databinding.ActivityProfileBinding
import com.example.readapp.ui.profile_edit.ProfileEditActivity
import com.example.readapp.utils.MainUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModel()

    private lateinit var adapterProfile: AdapterProfile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()

        adapterProfile = AdapterProfile(this, arrayListOf())
        binding.favoriteRv.adapter = adapterProfile

        viewModel.booksArrayList.observe(this) { books ->
            adapterProfile.updateBooks(books)
            binding.favoriteBooksTv.text = books.size.toString()
        }
        viewModel.loadFavoriteBooks()
        viewModel.loadUserInfo()

    }

    private fun setupObservers() {
        viewModel.userInfo.observe(this) { user ->
            if (user != null) {
                binding.nameTv.text = user.name
                binding.emailTv.text = user.email
                binding.accountTypeTv.text = user.userType
                binding.memberDateTv.text = MainUtils.formatTimeStamp(user.timestamp)

                try {
                    Glide.with(this@ProfileActivity)
                        .load(user.profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileIv)
                } catch (e: Exception) {
                    // Handle exception
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.profileEditBtn.setOnClickListener {
            startActivity(Intent(this, ProfileEditActivity::class.java))
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}