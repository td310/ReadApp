package com.example.readapp.data.repository.category

import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase

class CategoryRepository {

    fun addCategory(uid: String, category: String, timestamp: Long): Task<Void> {
        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "$timestamp"
        hashMap["category"] = category
        hashMap["timestamp"] = timestamp
        hashMap["uid"] = uid

        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        return ref.child("$timestamp").setValue(hashMap)
    }
}
