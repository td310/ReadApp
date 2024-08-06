package com.example.readapp.ui.dashboard_admin

import android.widget.Filter
import com.example.readapp.adapter.AdapterDashboardAdmin
import com.example.readapp.data.model.ModelCategory

class FilterDashboardAdmin :Filter {
    private var filterList: ArrayList<ModelCategory>

    public var adapterDashboardAdmin: AdapterDashboardAdmin

    constructor(filterList: ArrayList<ModelCategory>, adapterDashboardAdmin: AdapterDashboardAdmin) : super() {
        this.filterList = filterList
        this.adapterDashboardAdmin = adapterDashboardAdmin
    }

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        var constraint = constraint
        val results = FilterResults()

        if (constraint != null && constraint.isNotEmpty()) {
            constraint = constraint.toString().uppercase()
            val filteredModel: ArrayList<ModelCategory> = ArrayList()
            for (i in 0 until filterList.size) {
                if (filterList[i].category.uppercase().contains(constraint)) {
                    filteredModel.add(filterList[i])
                }
            }
            results.count = filteredModel.size
            results.values = filteredModel
        }
        else{
            //return list if constrain null
            results.count = filterList.size
            results.values = filterList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapterDashboardAdmin.categoryArrayList = results.values as ArrayList<ModelCategory>
        adapterDashboardAdmin.notifyDataSetChanged()
    }
}