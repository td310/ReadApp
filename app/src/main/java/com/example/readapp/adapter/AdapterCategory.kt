package com.example.readapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.readapp.R
import com.example.readapp.data.model.ModelCategory
import com.example.readapp.databinding.RowCategoryBinding
import com.example.readapp.ui.category.FilterCategory
import com.example.readapp.ui.pdf_admin.PdfListAdminActivity
import com.google.firebase.database.FirebaseDatabase

class AdapterCategory: RecyclerView.Adapter<AdapterCategory.HolderCategory>, Filterable {
    private val context: Context
    public var categoryArrayList: ArrayList<ModelCategory>
    private var filterList:ArrayList<ModelCategory>

    private var filter: FilterCategory? = null

    private lateinit var binding: RowCategoryBinding

    constructor(context: Context, categoryArrayList: ArrayList<ModelCategory>){
        this.context = context
        this.categoryArrayList = categoryArrayList
        this.filterList = categoryArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategory {
        binding = RowCategoryBinding.inflate(LayoutInflater.from(context),parent,false)
        return HolderCategory(binding.root)
    }

    override fun getItemCount(): Int {
        return categoryArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategory, position: Int) {
        val model = categoryArrayList[position]
        val id = model.id
        val category = model.category
        val uid = model.uid
        val timestamp = model.timestamp

        holder.categoryTv.text = category

        holder.deleteBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null)
            val builder = AlertDialog.Builder(context)
                .setView(dialogView)
            val alertDialog = builder.create()

            val cancelBtn = dialogView.findViewById<Button>(R.id.dialog_cancel)
            val confirmBtn = dialogView.findViewById<Button>(R.id.dialog_confirm)

            cancelBtn.setOnClickListener {
                alertDialog.dismiss()
            }

            confirmBtn.setOnClickListener {
                Toast.makeText(context, "Deleting", Toast.LENGTH_SHORT).show()
                deleteCategory(model, holder)
                alertDialog.dismiss()
            }

            alertDialog.show()
        }


        holder.itemView.setOnClickListener {
            val intent = Intent(context, PdfListAdminActivity::class.java)
            intent.putExtra("categoryId", id)
            intent.putExtra("category", category)
            context.startActivity(intent)
        }
    }

    private fun deleteCategory(model:ModelCategory,holder:HolderCategory) {
        val id = model.id
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(context,"Category have been deleted", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(context,"Unable to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    inner class HolderCategory(itemView: View): RecyclerView.ViewHolder(itemView) {
        var categoryTv: TextView = binding.categoryTv
        var deleteBtn: ImageButton = binding.deleteBtn
    }

    override fun getFilter(): Filter {
        if(filter == null){
            filter = FilterCategory(filterList,this)
        }
        return filter as FilterCategory
    }
}