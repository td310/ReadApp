package com.example.readapp.ui.pdf_user

import android.widget.Filter
import com.example.readapp.adapter.AdapterFragmentUser
import com.example.readapp.data.model.ModelPdf

class FilterPdfUser : Filter {
    var filterList: ArrayList<ModelPdf>
    var adapterFragmentUser: AdapterFragmentUser

    constructor(filterList: ArrayList<ModelPdf>, adapterFragmentUser: AdapterFragmentUser):super(){
        this.filterList = filterList
        this.adapterFragmentUser = adapterFragmentUser
    }

    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filteredModels= ArrayList<ModelPdf>()
            for (i in filterList.indices){
                if(filterList[i].title.uppercase().contains(constraint)){
                    filteredModels.add(filterList[i])
                }
            }
            results.count = filteredModels.size
            results.values = filteredModels
        }
        else{
            results.count = filterList.size
            results.values = filterList
        }
        return results

    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapterFragmentUser.pdfArrayList = results.values as ArrayList<ModelPdf>
        adapterFragmentUser.notifyDataSetChanged()
    }
}