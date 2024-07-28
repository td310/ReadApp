package com.example.readapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.databinding.RowPdfUserBinding
import com.example.readapp.ui.pdf_admin_list_detail.PdfListDetailActivity
import com.example.readapp.ui.pdf_user.FilterPdfUser
import com.example.readapp.utils.MainUtils

class AdapterPdfUser : RecyclerView.Adapter<AdapterPdfUser.HolderPdfUSer>, Filterable {

    private var context: Context

    public var pdfArrayList: ArrayList<ModelPdf>

    public var filterList: ArrayList<ModelPdf>

    private lateinit var binding: RowPdfUserBinding

    private var filter: FilterPdfUser? = null

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>) {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfUSer {
        binding = RowPdfUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfUSer(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfUSer, position: Int) {
        val model = pdfArrayList[position]
        val bookId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val uid = model.uid
        val url = model.url
        val timestamp = model.timestamp

        val date = MainUtils.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = date

        MainUtils.loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)
        MainUtils.loadCategory(categoryId, holder.categoryTv)
        MainUtils.loadPdfSize(url, title, holder.sizeTv)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListDetailActivity::class.java)
            intent.putExtra("bookId", bookId)
            context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = FilterPdfUser(filterList, this)
        }
        return filter as FilterPdfUser
    }

    inner class HolderPdfUSer(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var pdfView = binding.pdfView
        var progressBar = binding.progressBar
        var titleTv = binding.titleTv
        var descriptionTv = binding.descriptionTv
        var categoryTv = binding.categoryTv
        var sizeTv = binding.sizeTv
        var dateTv = binding.dateTv
    }
}