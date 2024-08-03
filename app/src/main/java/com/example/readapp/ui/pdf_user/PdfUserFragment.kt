package com.example.readapp.ui.pdf_user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.readapp.adapter.AdapterPdfUser
import com.example.readapp.databinding.FragmentBookUserBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PdfUserFragment : Fragment() {
    private lateinit var binding: FragmentBookUserBinding
    private val viewModel: PdfUserViewModel by viewModel()

    private var categoryId = ""
    private var category = ""
    private var uid = ""

    companion object {
        fun newInstance(categoryId: String, category: String, uid: String): PdfUserFragment {
            val fragment = PdfUserFragment()
            val args = Bundle()
            args.putString("categoryId", categoryId)
            args.putString("category", category)
            args.putString("uid", uid)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            categoryId = args.getString("categoryId")!!
            category = args.getString("category")!!
            uid = args.getString("uid")!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBookUserBinding.inflate(inflater, container, false)

        when (category) {
            "All" -> viewModel.fetchAllBooks()
            "Most Viewed" -> viewModel.fetchBooksByOrder("viewsCount")
            "Most Downloaded" -> viewModel.fetchBooksByOrder("downloadsCount")
            else -> viewModel.fetchBooksByCategory(categoryId)
        }

        viewModel.books.observe(viewLifecycleOwner) { books ->
            val adapter = AdapterPdfUser(requireContext(), ArrayList(books))
            binding.booksRv.adapter = adapter
        }

        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                (binding.booksRv.adapter as? AdapterPdfUser)?.filter?.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }
}

