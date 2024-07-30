package com.example.readapp.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.databinding.RowPdfAdminBinding
import com.example.readapp.ui.pdf_admin.FilterPdfAdmin
import android.widget.Filter
import android.widget.Filterable
import com.example.readapp.ui.pdf_admin_edit.PdfEditActivity
import com.example.readapp.ui.pdf_list_detail.PdfListDetailActivity
import com.example.readapp.utils.MainUtils
import java.util.*

class AdapterPdfAdmin : RecyclerView.Adapter<AdapterPdfAdmin.HolderPdfAdmin>,Filterable {
    private var context: Context

    //arraylist to hold  pdf
    public var pdfArrayList: ArrayList<ModelPdf>

    private val filterList:ArrayList<ModelPdf>

    private var filter: FilterPdfAdmin? = null

    private lateinit var binding: RowPdfAdminBinding

    constructor(context: Context, pdfArrayList: ArrayList<ModelPdf>):super() {
        this.context = context
        this.pdfArrayList = pdfArrayList
        this.filterList = pdfArrayList
    }

    inner class HolderPdfAdmin(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pdfView = binding.pdfView
        val progressBar = binding.progressBar
        val titleTv = binding.titleTv
        val descriptionTv = binding.descriptionTv
        val categoryTv = binding.categoryTv
        val sizeTv = binding.sizeTv
        val dateTv = binding.dateTv
        val moreBtn = binding.moreBtn
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfAdmin {
        binding = RowPdfAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderPdfAdmin(binding.root)
    }

    override fun onBindViewHolder(holder: HolderPdfAdmin, position: Int) {
        val model = pdfArrayList[position]
        val pdfId = model.id
        val categoryId = model.categoryId
        val title = model.title
        val description = model.description
        val pdfUrl = model.url
        val timestamp = model.timestamp
        //fun format time
        val formattedDate = MainUtils.formatTimeStamp(timestamp)

        holder.titleTv.text = title
        holder.descriptionTv.text = description
        holder.dateTv.text = formattedDate
        //fun...
        MainUtils.loadCategory(categoryId, holder.categoryTv)
        MainUtils.loadPdfFromUrlSinglePage(pdfUrl,title, holder.pdfView, holder.progressBar, null)
        MainUtils.loadPdfSize(pdfUrl, title,holder.sizeTv)

        holder.moreBtn.setOnClickListener {
            moreOptionsDialog(model, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListDetailActivity::class.java)
            intent.putExtra("bookId", pdfId)
            context.startActivity(intent)
        }
    }
    private fun moreOptionsDialog(model: ModelPdf, holder: HolderPdfAdmin) {
        val bookId = model.id
        val bookUrl = model.url
        val bookTitle = model.title

        val options = arrayOf("Edit", "Delete")

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose Options")
            .setItems(options) { dialog, position ->
                when (position) {
                    0 -> {
                        val intent = Intent(context, PdfEditActivity::class.java)
                        intent.putExtra("bookId", bookId)
                        context.startActivity(intent)
                    }
                    1 -> MainUtils.deleteBook(context, bookId, bookUrl, bookTitle)
                }
            }
            .show()
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterPdfAdmin(filterList,this)
        }
        return filter as FilterPdfAdmin
    }
    //update pdf list after searching
    fun updateList(newPdfList: ArrayList<ModelPdf>) {
        pdfArrayList.clear()
        pdfArrayList.addAll(newPdfList)
        notifyDataSetChanged()
    }
}

