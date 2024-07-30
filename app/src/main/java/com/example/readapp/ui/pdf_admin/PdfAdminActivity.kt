package com.example.readapp.ui.pdf_admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.readapp.adapter.AdapterPdfAdmin
import com.example.readapp.data.repository.pdf_admin.PdfAdminRepository
import com.example.readapp.databinding.ActivityPdfAdminBinding

class PdfAdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfAdminBinding

    private lateinit var viewModel: PdfAdminViewModel

    private lateinit var adapterPdfAdmin: AdapterPdfAdmin

    private companion object {
        const val TAG = "PDF_LIST_ADMIN_TAG"
    }

    private var categoryId = ""
    private var category = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        categoryId = intent.getStringExtra("categoryId")!!
        category = intent.getStringExtra("category")!!

        binding.subTitleTv.text = category

        val repository = PdfAdminRepository()
        val factory = PdfAdminViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(PdfAdminViewModel::class.java)

        adapterPdfAdmin = AdapterPdfAdmin(this, ArrayList())
        binding.booksRv.adapter = adapterPdfAdmin

        viewModel.loadPdfList(categoryId)
        observeViewModel()

        //back btn
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
        binding.backBtn.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        //search
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapterPdfAdmin.filter.filter(s)
                } catch (e: Exception) {
                    Log.d(TAG, "onTextChanged: ${e.message} ")
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.pdfList.observe(this, Observer { pdfList ->
            adapterPdfAdmin.updateList(pdfList)
        })
    }
}

