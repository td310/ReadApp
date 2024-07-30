package com.example.readapp.ui.pdf_list_detail

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.readapp.R
import com.example.readapp.adapter.AdapterComment
import com.example.readapp.data.model.ModelComment
import com.example.readapp.databinding.ActivityPdfListDetailBinding
import com.example.readapp.ui.pdf_admin_pdf_view.PdfViewActivity
import com.example.readapp.utils.MainUtils
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel


class PdfListDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfListDetailBinding

    private val viewModel: PdfListDetailViewModel by viewModel()

    private lateinit var progressDialog: AlertDialog

    private var bookId = ""

    private lateinit var firebaseAuth: FirebaseAuth

    private companion object {
        const val REQUEST_MANAGE_EXTERNAL_STORAGE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!

        //init fb
        firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser != null) {
            viewModel.checkIsFavorite(bookId)
        }

        setupProgressDialog()
        setupObservers()
        setupListeners()

        viewModel.loadBookDetails(bookId)

    }


    private fun setupObservers() {
        viewModel.bookDetails.observe(this) { book ->
            binding.titleTv.text = book.title
            binding.descriptionTv.text = book.description
            binding.viewsTv.text = book.viewsCount.toString()
            binding.downloadTv.text = book.downloadsCount.toString()
            binding.dateTv.text = MainUtils.formatTimeStamp(book.timestamp)

            MainUtils.loadCategory(book.categoryId, binding.categoryTv)
            MainUtils.loadPdfFromUrlSinglePage(
                book.url,
                book.title,
                binding.pdfView,
                binding.progressBar,
                binding.pagesTv
            )
            MainUtils.loadPdfSize(book.url, book.title, binding.sizeTv)
            MainUtils.incrementBookViewCount(bookId)

            viewModel.isInMyFavorite.observe(this) { isInMyFavorite ->
                if (isInMyFavorite) {
                    binding.favbookBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_favorite_white,
                        0,
                        0
                    )
                    binding.favbookBtn.text = "Remove Favorite"
                } else {
                    binding.favbookBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        R.drawable.ic_favorite_border_white,
                        0,
                        0
                    )
                    binding.favbookBtn.text = "Add Favorite"
                }
            }

            viewModel.comments.observe(this) { commentsList ->
                val adapter = AdapterComment(this, commentsList)
                binding.commentsRv.adapter = adapter
            }

            viewModel.commentAdded.observe(this) { isAdded ->
                if (isAdded) {
                    binding.commentEt.text.clear()
                    Toast.makeText(this, "Comment added", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.downloadStatus.observe(this) { success ->
            progressDialog.dismiss()
            if (success) {
                Toast.makeText(this, "Book downloaded successfully", Toast.LENGTH_SHORT).show()
                viewModel.incrementDownloadCount(bookId)
            } else {
                Toast.makeText(this, "Failed to download book", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.readBookBtn.setOnClickListener {
            val intent = Intent(this, PdfViewActivity::class.java)
            intent.putExtra("bookId", bookId)
            startActivity(intent)
        }

        binding.downloadBookBtn.setOnClickListener {
            checkPermissionsAndDownload()
        }

        binding.favbookBtn.setOnClickListener {
            if (viewModel.isInMyFavorite.value == true) {
                viewModel.removeFromFavorite(bookId)
            } else {
                viewModel.addToFavorite(bookId)
            }
        }

        binding.submitBtn.setOnClickListener {
            val comment = binding.commentEt.text.toString().trim()
            if (comment.isNotEmpty()) {
                val modelComment = ModelComment(
                    id = System.currentTimeMillis().toString(),
                    bookId = bookId,
                    timestamp = System.currentTimeMillis().toString(),
                    uid = firebaseAuth.uid!!,
                    comment = comment
                )
                viewModel.addComment(modelComment)
            } else {
                Toast.makeText(this, "Please enter comment", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.fetchComments(bookId)
    }

    private fun setupProgressDialog() {
        val progressDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
        progressDialog = AlertDialog.Builder(this)
            .setTitle("Please Wait")
            .setView(progressDialogView)
            .setCancelable(false)
            .create()
    }

    private fun checkPermissionsAndDownload() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                //show progress
                progressDialog.setTitle("Downloading Book")
                progressDialog.show()
                viewModel.downloadBook()
            } else {
                requestManageExternalStoragePermission()
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.downloadBook()
            } else {
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    private fun requestManageExternalStoragePermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse(String.format("package:%s", packageName))
            startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE)
        } catch (e: Exception) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    //show progress
                    progressDialog.setTitle("Downloading Book")
                    progressDialog.show()
                    viewModel.downloadBook()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            //show progress
            progressDialog.setTitle("Downloading Book")
            progressDialog.show()
            viewModel.downloadBook()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
