package com.example.readapp.ui.profile_edit

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Toast
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.readapp.R
import com.example.readapp.databinding.ActivityProfileEditBinding
import com.example.readapp.databinding.DialogChangePasswordBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private val viewModel: ProfileEditViewModel by viewModel()

    private lateinit var progressDialog: AlertDialog

    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val progressDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        progressDialog = AlertDialog.Builder(this)
            .setTitle("Please Wait")
            .setView(progressDialogView)
            .setCancelable(false)
            .create()

        setupObservers()
        setupClickListeners()

        viewModel.loadUserInfo()
    }

    private fun setupObservers() {
        viewModel.userInfo.observe(this) { user ->
            if (user != null) {
                binding.nameEt.setText(user.name)
                try {
                    Glide.with(this)
                        .load(user.profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileIv)
                } catch (e: Exception) {
                    // Handle exception
                }
            }
        }

        viewModel.updateStatus.observe(this) { success ->
            progressDialog.dismiss()
            if (success) {
                Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.uploadImageUrl.observe(this) { imageUrl ->
            if (imageUrl != null) {
                updateProfile(imageUrl)
            } else {
                progressDialog.dismiss()
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.profileIv.setOnClickListener {
            showImageAttachMenu()
        }

        binding.updateBtn.setOnClickListener {
            validateData()
        }
        //change password
        binding.optionalTv.setOnClickListener {
            addDialogPassword()
        }
    }

    private var repass = ""
    private fun addDialogPassword() {
        val dialogBinding = DialogChangePasswordBinding.inflate(layoutInflater)

        val builder = androidx.appcompat.app.AlertDialog.Builder(this,R.style.CustomDialog)
        builder.setView(dialogBinding.root)

        val alertDialog = builder.create()
        alertDialog.show()

        dialogBinding.dialogCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        dialogBinding.dialogConfirm.setOnClickListener {
            val currentPassword = dialogBinding.oldpasswordEt.text.toString().trim()
            val newPassword = dialogBinding.passwordEt.text.toString().trim()
            val confirmNewPassword = dialogBinding.confirmpasswordEt.text.toString().trim()

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmNewPassword) {
                Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            } else {

            }
        }
    }


    private var name = ""
    private fun validateData() {
        name = binding.nameEt.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
        } else {
            if (imageUri == null) {
                updateProfile("")
            } else {
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        progressDialog.setTitle("Uploading profile image...")
        progressDialog.show()
        imageUri?.let {
            viewModel.uploadImage(it)
        }
    }

    private fun updateProfile(uploadedImageUrl: String) {
        progressDialog.setTitle("Updating profile...")
        progressDialog.show()
        viewModel.updateProfile(name, if (imageUri == null) null else uploadedImageUrl)
    }

    private fun showImageAttachMenu() {
        val popupMenu = PopupMenu(this, binding.profileIv)
        popupMenu.menu.add(Menu.NONE, 0, 0, "Camera")
        popupMenu.menu.add(Menu.NONE, 1, 1, "Gallery")
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            val id = item.itemId
            if (id == 0) {
                //camera clicked
                pickImageCamera()
            } else if (id == 1) {
                //gallery clicked
                pickImageGallery()
            }
            true
        }
    }

    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private fun pickImageCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Temp_Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Temp_Description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open the camera
                pickImageCamera()
            } else {
                // Permission denied, show a message or disable camera functionality
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        galleryActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            //get uri of img
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                //set to imgView
                binding.profileIv.setImageURI(imageUri)
            }
            else{
                //cancelled
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    )

    private var galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            //get uri of img
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                imageUri = data!!.data
                //set to imgView
                binding.profileIv.setImageURI(imageUri)
            }
            else{
                //cancelled
                Toast.makeText(this,"Cancelled",Toast.LENGTH_SHORT).show()
            }
        }
    )
}