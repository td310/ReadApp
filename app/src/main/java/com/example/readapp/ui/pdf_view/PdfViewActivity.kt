package com.example.readapp.ui.pdf_view

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.readapp.databinding.ActivityPdfViewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdfViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfViewBinding
    private val pdfViewViewModel: PdfViewViewModel by viewModel()
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
        pdfViewViewModel.loadBookDetails(bookId)

        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        pdfViewViewModel.pdfBytes.observe(this, { bytes ->
            binding.pdfView.fromBytes(bytes)
                .swipeHorizontal(false)
                .onPageChange { page, pageCount ->
                    val currentPage = page + 1
                    pdfViewViewModel.updatePageTitle(currentPage, pageCount)
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

        pdfViewViewModel.errorMessage.observe(this, { message ->
            binding.progressBar.visibility = View.GONE
        })

        pdfViewViewModel.pageTitle.observe(this, { title ->
            binding.toolbarSubTitleTv.text = title
        })
    }
}