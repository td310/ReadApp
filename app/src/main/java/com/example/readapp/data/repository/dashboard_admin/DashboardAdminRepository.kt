package com.example.readapp.data.repository.dashboard_admin

import android.view.Display.Mode
import com.example.readapp.data.model.ModelCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DashboardAdminRepository(private val firebaseDatabase: FirebaseDatabase) {

    fun loadCategories(onSuccess: (List<ModelCategory>) -> Unit){
        firebaseDatabase.getReference("Categories")
            .addValueEventListener(object :ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val categories = mutableListOf<ModelCategory>()
                        for (ds in snapshot.children) {
                            val model = ds.getValue(ModelCategory::class.java)
                            if (model != null) {
                                categories.add(model)
                            }
                            onSuccess(categories)
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })
    }
}