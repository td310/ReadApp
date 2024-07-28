package com.example.readapp.ui.pdf_admin

import com.example.readapp.adapter.AdapterPdfListAdmin
import com.example.readapp.data.model.ModelPdf
import android.widget.Filter

class FilterPdfAdmin : Filter {
    var filterList: ArrayList<ModelPdf>

    var adapterPdfAdmin:AdapterPdfListAdmin

    constructor(filterList: ArrayList<ModelPdf>,adapterPdfAdmin: AdapterPdfListAdmin){
        this.filterList = filterList
        this.adapterPdfAdmin = adapterPdfAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint:CharSequence? = constraint

        val results = FilterResults()
        if(constraint != null && constraint.isNotEmpty()){
            //change uppercase or lower
            constraint = constraint.toString().lowercase()
            var filteredModels = ArrayList<ModelPdf>()
            for (i in filterList.indices){
                //validate if match
                if(filterList[i].title.lowercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            //search value null or empty
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterPdfAdmin.pdfArrayList = results.values as ArrayList<ModelPdf>
        adapterPdfAdmin.notifyDataSetChanged()
    }
}
