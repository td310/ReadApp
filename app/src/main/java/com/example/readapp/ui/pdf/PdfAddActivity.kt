package com.example.readapp.ui.pdf

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.R
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.databinding.ActivityPdfAddBinding
import com.example.readapp.ui.dashboard_admin.DashboardAdminActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdfAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAddBinding

    private lateinit var progressDialog: ProgressDialog

    private val pdfAddViewModel: PdfAddViewModel by viewModel()

    private var pdfUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAddBinding.inflate(layoutInflater)
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

        binding.categoryTv.setOnClickListener {
            categoryPickDialog()
        }

        binding.attachPdfBtn.setOnClickListener {
            pdfPickIntent()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }

        pdfAddViewModel.loadPdfCategories()
        observeViewModel()
    }

    private var isPdfUploadSuccessHandled = false

    private fun observeViewModel() {
        pdfAddViewModel.uploadState.observe(this, { isSuccess ->
            progressDialog.dismiss()
            if (isSuccess) {
                if (!isPdfUploadSuccessHandled) {
                    Toast.makeText(this, "PDF Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    isPdfUploadSuccessHandled = true
                    uploadPdfInfoToFirebase()
                    pdfUri = null
                    startActivity(Intent(this, DashboardAdminActivity::class.java))
                    finish()
                }
            } else {
                // Handle upload failure
                Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                isPdfUploadSuccessHandled = false
            }
        })

        pdfAddViewModel.errorMessage.observe(this, { message ->
            Toast.makeText(this, "Failed to upload due to $message", Toast.LENGTH_SHORT).show()
        })

        pdfAddViewModel.categories.observe(this, { categories ->
            categoryArrayList.clear()
            categoryArrayList.addAll(categories)
        })
    }



    private var title = ""
    private var description = ""
    private var category = ""
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    private val categoryArrayList = ArrayList<ModelCategory>()

    private fun validateData() {
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        category = binding.categoryTv.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
        } else if (category.isEmpty()) {
            Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show()
        } else if (pdfUri == null) {
            Toast.makeText(this, "Pick PDF", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.show()
            pdfAddViewModel.uploadPdfToFirebaseStorage(pdfUri!!)
        }
    }

    private fun uploadPdfInfoToFirebase() {
        pdfAddViewModel.uploadPdfInfoToFirebase(
            title,
            description,
            selectedCategoryId
        )
    }

    private fun categoryPickDialog() {
        val categoriesArray = arrayOfNulls<String>(categoryArrayList.size)
        for (i in categoryArrayList.indices) {
            categoriesArray[i] = categoryArrayList[i].category
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Category")
            .setItems(categoriesArray) { _, which ->
                selectedCategoryId = categoryArrayList[which].id
                selectedCategoryTitle = categoryArrayList[which].category
                binding.categoryTv.text = selectedCategoryTitle
            }.show()
    }

    private fun pdfPickIntent() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivityResultLauncher.launch(intent)
    }

    private val pdfActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> { result ->
            if (result.resultCode == RESULT_OK) {
                pdfUri = result.data!!.data
            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )
}

