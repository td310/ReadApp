package com.example.readapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.databinding.RowProfileFavoriteBinding
import com.example.readapp.ui.pdf_list_detail.PdfListDetailActivity
import com.example.readapp.ui.profile.ProfileActivity
import com.example.readapp.utils.MainUtils

class AdapterProfile(
    private val context: Context,
    private var booksArrayList: ArrayList<ModelPdf>
) : RecyclerView.Adapter<AdapterProfile.HolderPdfFavorite>() {

    private lateinit var binding: RowProfileFavoriteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfFavorite {
        binding = RowProfileFavoriteBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfFavorite(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfFavorite, position: Int) {
        val model = booksArrayList[position]

        MainUtils.loadBookDetails(model, holder)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListDetailActivity::class.java)
            intent.putExtra("bookId", model.id)
            context.startActivity(intent)
        }

        holder.removeFavBtn.setOnClickListener {
            val profileActivity = context as? ProfileActivity
            if (profileActivity != null) {
                MainUtils.removeFromFavorite(context, model.id)
            }
        }
    }

    override fun getItemCount(): Int {
        return booksArrayList.size
    }

    fun updateBooks(newBooks: ArrayList<ModelPdf>) {
        booksArrayList = newBooks
        notifyDataSetChanged()
    }

    inner class HolderPdfFavorite(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfThumbnailIv = binding.pdfThumbnailIv
        var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var removeFavBtn = binding.removeFavBtn
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv

    }
}