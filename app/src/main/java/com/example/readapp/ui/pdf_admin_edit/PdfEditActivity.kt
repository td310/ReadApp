package com.example.readapp.ui.pdf_admin_edit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.R
import com.example.readapp.databinding.ActivityPdfEditBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdfEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfEditBinding
    private val viewModel: PdfEditViewModel by viewModel()

    private var bookId = ""
    private lateinit var progressDialog: AlertDialog
    private lateinit var categoryTitleArrayList: ArrayList<String>
    private lateinit var categoryIdArrayList: ArrayList<String>
    private var selectedCategoryId = ""
    private var selectedCategoryTitle = ""
    private var title = ""
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!

        val progressDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        progressDialog = AlertDialog.Builder(this)
            .setTitle("Please Wait")
            .setView(progressDialogView)
            .setCancelable(false)
            .create()

        observeViewModel()
        viewModel.loadCategories()
        viewModel.loadBookInfo(bookId)

        //back btn
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.categoryTv.setOnClickListener {
            categoryDialog()
        }

        binding.submitBtn.setOnClickListener {
            validateData()
        }
    }

    private fun observeViewModel() {
        viewModel.categories.observe(this, { categories ->
            categoryTitleArrayList = ArrayList(categories.map { it.second })
            categoryIdArrayList = ArrayList(categories.map { it.first })
        })

        viewModel.bookInfo.observe(this, { bookInfo ->
            selectedCategoryId = bookInfo.first
            description = bookInfo.second
            title = bookInfo.third
            binding.titleEt.setText(title)
            binding.descriptionEt.setText(description)
            binding.categoryTv.text = selectedCategoryTitle
        })

        viewModel.updateStatus.observe(this, { success ->
            progressDialog.dismiss()
            if (success) {
                Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to update", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.error.observe(this, { errorMessage ->
            progressDialog.dismiss()
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun validateData() {
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()

        if (title.isEmpty()) {
            Toast.makeText(this, "Enter Book Title", Toast.LENGTH_SHORT).show()
        } else if (description.isEmpty()) {
            Toast.makeText(this, "Enter Book Description", Toast.LENGTH_SHORT).show()
        } else if (selectedCategoryId.isEmpty()) {
            Toast.makeText(this, "Choose Category", Toast.LENGTH_SHORT).show()
        } else {
            progressDialog.setTitle("Updating")
            progressDialog.show()
            viewModel.updatePdf(bookId, title, description, selectedCategoryId)
        }
    }

    private fun categoryDialog() {
        val categoryArray = categoryTitleArrayList.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Choose Category")
            .setItems(categoryArray) { _, position ->
                selectedCategoryTitle = categoryTitleArrayList[position]
                selectedCategoryId = categoryIdArrayList[position]
                binding.categoryTv.text = selectedCategoryTitle
            }
            .show()
    }
}