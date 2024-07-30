package com.example.readapp.data.repository.pdf_admin

import com.example.readapp.data.model.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfAdminRepository() {
    private val database = FirebaseDatabase.getInstance().getReference("Books")

    fun getPdfsByCategory(categoryId: String, callback: (ArrayList<ModelPdf>) -> Unit) {
        val pdfArrayList = ArrayList<ModelPdf>()
        database.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children) {
                        val model = ds.getValue(ModelPdf::class.java)
                        if (model != null) {
                            pdfArrayList.add(model)
                        }
                    }
                    callback(pdfArrayList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}