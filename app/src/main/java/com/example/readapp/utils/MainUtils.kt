package com.example.readapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.readapp.R
import com.example.readapp.adapter.AdapterProfile
import com.example.readapp.data.model.ModelComment
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.databinding.RowCommentBinding
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
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
                        Log.d(TAG, "deleteBook: Deleted from database")
                        // Remove from favorites of all users
                        removeFromFavorite(context, bookId) {
                            Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.d(TAG, "deleteBook: Failed to delete from database due to ${e.message}")
                        Toast.makeText(
                            context,
                            "Failed to delete from database due to ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "deleteBook: Failed to delete due to ${e.message}")
                Toast.makeText(context, "Failed to delete due to ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Remove from favorite for all users
    fun removeFromFavorite(context: Context, bookId: String, onSuccess: () -> Unit = {}) {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    userSnapshot.ref.child("Favorites").child(bookId)
                        .removeValue()
                        .addOnSuccessListener {
                            Log.d(TAG, "removeFromAllFavorites: Removed from favorite of user ${userSnapshot.key}")
                        }
                        .addOnFailureListener { e ->
                            Log.d(TAG, "removeFromAllFavorites: Failed to remove from favorite of user ${userSnapshot.key} due to ${e.message}")
                        }
                }
                onSuccess()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "removeFromAllFavorites: Failed to remove from favorites due to ${error.message}")
            }
        })
    }

    //delete books in category
    fun deleteBooksInCategory(context: Context, categoryId: String, onSuccess: () -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("Books")
        ref.orderByChild("categoryId").equalTo(categoryId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (bookSnapshot in snapshot.children) {
                        val bookId = bookSnapshot.key ?: continue
                        val bookUrl = bookSnapshot.child("url").getValue(String::class.java) ?: continue
                        val bookTitle = bookSnapshot.child("title").getValue(String::class.java) ?: continue
                        deleteBook(context, bookId, bookUrl, bookTitle)
                    }
                    onSuccess()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "deleteBooksInCategory: Failed to delete books in category due to ${error.message}")
                }
            })
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

                    loadPdfThumbnail(url, holder.pdfThumbnailIv, holder.progressBar)
                    loadCategory(categoryId, holder.categoryTv)
                    loadPdfSize(url, title, holder.sizeTv)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
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


    fun loadPdfThumbnail(pdfUrl: String, imageView: ImageView, progressBar: View) {
        val cacheFile = File(imageView.context.cacheDir, "${pdfUrl.hashCode()}.pdf")

        if (cacheFile.exists()) {
            loadThumbnailFromCache(cacheFile, imageView, progressBar)
            return
        }

        progressBar.visibility = View.VISIBLE
        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bytes = ref.getBytes(MAX_BYTES_PDF).await()
                try {
                    val fos = FileOutputStream(cacheFile)
                    fos.write(bytes)
                    fos.close()
                    loadThumbnailFromCache(cacheFile, imageView, progressBar)
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        progressBar.visibility = View.INVISIBLE
                        Log.d(TAG, "Failed to save file to cache due to ${e.message}")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.INVISIBLE
                    Log.d(TAG, "Failed to get bytes due to ${e.message}")
                }
            }
        }
    }

    private fun loadThumbnailFromCache(file: File, imageView: ImageView, progressBar: View) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pdfRenderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
                val page = pdfRenderer.openPage(0)
                val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                pdfRenderer.close()

                withContext(Dispatchers.Main) {
                    imageView.setImageBitmap(bitmap)
                    progressBar.visibility = View.INVISIBLE
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.INVISIBLE
                    Log.d(TAG, "Failed to load thumbnail from cache due to ${e.message}")
                }
            }
        }
    }
}