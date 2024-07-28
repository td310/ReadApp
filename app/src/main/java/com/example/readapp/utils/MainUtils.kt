package com.example.readapp.utils

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.readapp.R
import com.example.readapp.adapter.AdapterProfile
import com.example.readapp.data.model.ModelComment
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.databinding.RowCommentBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

object MainUtils {

    private const val TAG = "PDF_UTILS_TAG"
    private const val MAX_BYTES_PDF: Long = 5000000000

    fun formatTimeStamp(timestamp: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        return DateFormat.format("dd/MM/yyyy", cal).toString()
    }

    //delete book
    fun deleteBook(context: Context, bookId: String, bookUrl: String, bookTitle: String) {
        Log.d(TAG, "deleteBook: Deleting...")

        val progressDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null)
        val progressMessage: TextView = progressDialogView.findViewById(R.id.progress_message)
        progressMessage.text = "Deleting $bookTitle"

        val progressDialog = AlertDialog.Builder(context)
            .setTitle("Please Wait")
            .setView(progressDialogView)
            .setCancelable(false)
            .create()

        progressDialog.show()

        Log.d(TAG, "deleteBook: Deleting from storage...")
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(bookUrl)
        storageReference.delete()
            .addOnSuccessListener {
                Log.d(TAG, "deleteBook: Deleted from storage")
                Log.d(TAG, "deleteBook: Deleting from database...")

                val ref = FirebaseDatabase.getInstance().getReference("Books")
                ref.child(bookId)
                    .removeValue()
                    .addOnSuccessListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "deleteBook: Deleted from database")
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "deleteBook: Failed to delete from database due to ${e.message}")
                        Toast.makeText(
                            context,
                            "Failed to delete from database due to ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressDialog.dismiss()
                    }
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Log.d(TAG, "deleteBook: Failed to delete due to ${e.message}")
                Toast.makeText(context, "Failed to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    //load size pdf
    fun loadPdfSize(pdfUrl: String,pdfTitle: String, sizeTv: TextView) {
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        ref.metadata
            .addOnSuccessListener { storageMetadata ->
                Log.d(TAG, "loadPdfSize: Got Metadata")
                val bytes = storageMetadata.sizeBytes.toDouble()
                Log.d(TAG, "loadPdfSize: Size Bytes $bytes")

                val kb = bytes / 1024
                val mb = kb / 1024
                sizeTv.text = when {
                    mb >= 1 -> "${String.format("%.2f", mb)} MB"
                    kb >= 1 -> "${String.format("%.2f", kb)} KB"
                    else -> "${bytes} Bytes"
                }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadPdfSize: Failed to get metadata due to ${e.message}")
            }
    }

    //show only 1 page -> optimize resources
    fun loadPdfFromUrlSinglePage(
        pdfUrl: String,
        pdfTitle: String,
        pdfView: PDFView,
        progressBar: View,
        pagesTv: TextView?
    ) {
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)

        ref.getBytes(MAX_BYTES_PDF)
            .addOnSuccessListener { bytes ->
                pdfView.fromBytes(bytes)
                    .spacing(0)
                    .swipeHorizontal(false)
                    .enableSwipe(false)
                    .onError { t ->
                        progressBar.visibility = View.INVISIBLE
                        Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                    }
                    .onPageError { page, t ->
                        progressBar.visibility = View.VISIBLE
                        Log.d(TAG, "loadPdfFromUrlSinglePage: ${t.message}")
                    }
                    .onLoad { nbPages ->
                        progressBar.visibility = View.INVISIBLE
                        pagesTv?.text = "$nbPages"
                    }
                    .load()
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "loadPdfFromUrlSinglePage: Failed to get bytes due to ${e.message}")
            }
    }

    //show pdf belong to categoryId
    fun loadCategory(categoryId: String, categoryTv: TextView) {
        val ref = FirebaseDatabase.getInstance().getReference("Categories")
        ref.child(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val category: String = "${snapshot.child("category").value}"
                    categoryTv.text = category
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "loadCategory: Failed to load category due to ${error.message}")
                }
            })
    }

    //increment view count
    fun incrementBookViewCount(bookId:String){
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //1.get view count
                    var viewsCount = "${snapshot.child("viewsCount").value}"
                    if(viewsCount == "" || viewsCount == "null"){
                        viewsCount = "0"
                    }
                    //2.increment view count
                    val newViewsCount = viewsCount.toLong() + 1

                    val hashMap = HashMap<String, Any>()
                    hashMap["viewsCount"] = newViewsCount

                    val dbRef = FirebaseDatabase.getInstance().getReference("Books")
                    dbRef.child(bookId)
                        .updateChildren(hashMap)

                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun loadBookDetails(model: ModelPdf, holder: AdapterProfile.HolderPdfFavorite) {
        val bookId = model.id

        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.child(bookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val categoryId = snapshot.child("categoryId").value.toString()
                    val description = snapshot.child("description").value.toString()
                    val downloadsCount = snapshot.child("downloadsCount").value.toString()
                    val timestamp = snapshot.child("timestamp").value.toString()
                    val title = snapshot.child("title").value.toString()
                    val uid = snapshot.child("uid").value.toString()
                    val url = snapshot.child("url").value.toString()
                    val viewsCount = snapshot.child("viewsCount").value.toString()

                    val date = formatTimeStamp(timestamp.toLong())

                    model.isFavorite = true
                    model.title = title
                    model.description = description
                    model.categoryId = categoryId
                    model.timestamp = timestamp.toLong()
                    model.url = url
                    model.uid = uid
                    model.viewsCount = viewsCount.toLong()
                    model.downloadsCount = downloadsCount.toLong()

                    holder.titleTv.text = title
                    holder.descriptionTv.text = description
                    holder.dateTv.text = date

                    loadPdfFromUrlSinglePage(url, title, holder.pdfView, holder.progressBar, null)
                    loadCategory(categoryId, holder.categoryTv)
                    loadPdfSize(url, title, holder.sizeTv)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    fun removeFromFavorite(context: Context,bookId: String) {
        val TAG = "REMOVE_FAV_TAG"
        Log.d(TAG, "removeFromFavorite: Removing from favorite")

        val firebaseAuth = FirebaseAuth.getInstance()

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(firebaseAuth.uid!!).child("Favorites").child(bookId)
            .removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "removeFromFavorite: Removed form favorite")
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "removeFromFavorite: Failed to remove to favorite due to ${e.message}")
                Toast.makeText(
                    context,
                    "Failed to remove to favorite due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

//AdapterComment
    fun loadUserDetails(model: ModelComment, binding: RowCommentBinding) {
        val ref = FirebaseDatabase.getInstance().getReference("Users").child(model.uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = "${snapshot.child("name").value}"
                val profileImage = "${snapshot.child("profileImage").value}"

                binding.nameTv.text = name
                try {
                    Glide.with(binding.root.context)
                        .load(profileImage)
                        .placeholder(R.drawable.ic_person_gray)
                        .into(binding.profileIv)
                } catch (e: Exception) {
                    binding.profileIv.setImageResource(R.drawable.ic_person_gray)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun deleteCommentDialog(context: Context, model: ModelComment) {
        AlertDialog.Builder(context)
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("Delete") { dialog, _ ->
                val ref = FirebaseDatabase.getInstance().getReference("Books").child(model.bookId).child("Comments").child(model.id)
                ref.removeValue().addOnSuccessListener {
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to delete comment due to ${e.message}", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}