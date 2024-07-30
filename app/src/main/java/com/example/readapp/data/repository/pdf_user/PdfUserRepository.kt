package com.example.readapp.data.repository.pdf_user

import com.example.readapp.data.model.ModelCategory
import com.example.readapp.data.model.ModelPdf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PdfUserRepository(private val database: FirebaseDatabase) {

    fun fetchCategories(callback: (List<ModelCategory>) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<ModelCategory>()
                for (ds in snapshot.children) {
                    val model = ds.getValue(ModelCategory::class.java)
                    if (model != null) {
                        categories.add(model)
                    }
                }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(emptyList())
            }
        })
    }

    fun getAllBooks(callback: (List<ModelPdf>) -> Unit) {
        val ref = database.getReference("Books")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bookList = snapshot.children.mapNotNull { it.getValue(ModelPdf::class.java) }
                callback(bookList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getBooksByOrder(orderBy: String, callback: (List<ModelPdf>) -> Unit) {
        val ref = database.getReference("Books")
        ref.orderByChild(orderBy).limitToLast(10)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookList = snapshot.children.mapNotNull { it.getValue(ModelPdf::class.java) }
                    callback(bookList.sortedByDescending { if (orderBy == "viewsCount") it.viewsCount else it.downloadsCount })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun getBooksByCategory(categoryId: String, callback: (List<ModelPdf>) -> Unit) {
        val ref = database.getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val bookList = snapshot.children.mapNotNull { it.getValue(ModelPdf::class.java) }
                    callback(bookList)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}