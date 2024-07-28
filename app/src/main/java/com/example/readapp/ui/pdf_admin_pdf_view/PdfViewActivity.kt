package com.example.readapp.ui.pdf_admin_pdf_view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.databinding.ActivityPdfViewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdfViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewBinding
    private val pdfViewDetailViewModel: PdfViewDetailViewModel by viewModel()
    private var bookId = ""

    private companion object {
        const val TAG = "PDF_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bookId = intent.getStringExtra("bookId")!!
        observeViewModel()
        pdfViewDetailViewModel.loadBookDetails(bookId)

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        pdfViewDetailViewModel.pdfBytes.observe(this, { bytes ->
            binding.pdfView.fromBytes(bytes)
                .swipeHorizontal(false)
                .onPageChange { page, pageCount ->
                    val currentPage = page + 1
                    pdfViewDetailViewModel.updatePageTitle(currentPage, pageCount)
                }
                .onError {
                    Log.d(TAG, "loadBookFromUrl: Failed to get book from url")
                }
                .onPageError { page, t ->
                    Log.d(TAG, "loadBookFromUrl: Failed to get page due to ${t.message}")
                }
                .load()
            binding.progressBar.visibility = View.GONE
        })

        pdfViewDetailViewModel.errorMessage.observe(this, { message ->
            Log.d(TAG, "Error: $message")
            binding.progressBar.visibility = View.GONE
        })

        pdfViewDetailViewModel.pageTitle.observe(this, { title ->
            binding.toolbarSubTitleTv.text = title
        })
    }
}