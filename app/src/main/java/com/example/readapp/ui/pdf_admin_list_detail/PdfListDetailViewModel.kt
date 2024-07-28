package com.example.readapp.ui.pdf_admin_list_detail

import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.readapp.data.model.ModelComment
import com.example.readapp.data.model.ModelPdf
import com.example.readapp.data.repository.pdf_admin_list_detail.PdfListAdminRepository
import java.io.FileOutputStream

class PdfListDetailViewModel(private val repository: PdfListAdminRepository) : ViewModel() {

    private val _bookDetails = MutableLiveData<ModelPdf>()
    val bookDetails: LiveData<ModelPdf> get() = _bookDetails

    private val _downloadStatus = MutableLiveData<Boolean>()
    val downloadStatus: LiveData<Boolean> get() = _downloadStatus

    private val _isInMyFavorite = MutableLiveData<Boolean>()
    val isInMyFavorite: LiveData<Boolean> get() = _isInMyFavorite

    private val _comments = MutableLiveData<List<ModelComment>>()
    val comments: LiveData<List<ModelComment>> = _comments

    private val _commentAdded = MutableLiveData<Boolean>()
    val commentAdded: LiveData<Boolean> = _commentAdded

    private var bookUrl: String? = null

    fun loadBookDetails(bookId: String) {
        repository.getBookDetails(bookId) { book ->
            _bookDetails.postValue(book)
            bookUrl = book.url
        }
    }

    fun downloadBook() {
        val url = bookUrl
        if (url.isNullOrEmpty()) {
            _downloadStatus.postValue(false)
            return
        }
        repository.downloadBook(url) { bytes ->
            if (bytes != null) {
                saveToDownloadsFolder(bytes, _bookDetails.value?.title ?: "book")
                _downloadStatus.postValue(true)
            } else {
                _downloadStatus.postValue(false)
            }
        }
    }


    private fun saveToDownloadsFolder(bytes: ByteArray, bookTitle: String) {
        val sanitizedBookTitle = bookTitle.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
        val nameWithExtension = "${sanitizedBookTitle}.pdf"

        try {
            val downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            downloadFolder.mkdirs()
            val filePath = downloadFolder.path + "/" + nameWithExtension
            val out = FileOutputStream(filePath)
            out.write(bytes)
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun incrementDownloadCount(bookId: String) {
        repository.incrementDownloadCount(bookId)
    }

    fun checkIsFavorite(bookId: String) {
        repository.checkIsFavorite(bookId) { isFavorite ->
            _isInMyFavorite.value = isFavorite
        }
    }

    fun addToFavorite(bookId: String) {
        repository.addToFavorite(bookId, {
            _isInMyFavorite.value = true
        }, { e ->
            _isInMyFavorite.value = false
        })
    }

    fun removeFromFavorite(bookId: String) {
        repository.removeFromFavorite(bookId, {
            _isInMyFavorite.value = false
        }, { e ->
            _isInMyFavorite.value = true
        })
    }

    fun addComment(comment: ModelComment) {
        repository.addComment(comment) {
            _commentAdded.postValue(true)
        }
    }

    fun fetchComments(bookId: String) {
        repository.fetchComments(bookId) { commentsList ->
            _comments.postValue(commentsList)
        }
    }
}