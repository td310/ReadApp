package com.example.readapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.readapp.data.model.ModelComment
import com.example.readapp.databinding.RowCommentBinding
import com.example.readapp.utils.MainUtils
import com.google.firebase.auth.FirebaseAuth

class AdapterComment(
    private val context: Context,
    private val commentArrayList: List<ModelComment>
) : RecyclerView.Adapter<AdapterComment.HolderComment>() {
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderComment {
        val binding = RowCommentBinding.inflate(LayoutInflater.from(context), parent, false)
        return HolderComment(binding)
    }

    override fun onBindViewHolder(holder: HolderComment, position: Int) {
        val model = commentArrayList[position]
        holder.bind(model)
    }

    override fun getItemCount(): Int = commentArrayList.size

    inner class HolderComment(private val binding: RowCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ModelComment) {
            firebaseAuth = FirebaseAuth.getInstance()

            binding.dateTv.text = MainUtils.formatTimeStamp(model.timestamp.toLong())
            binding.commentTv.text = model.comment
            MainUtils.loadUserDetails(model, binding)
            binding.deleteBtn.apply {
                if (firebaseAuth.currentUser?.uid == model.uid) {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        MainUtils.deleteCommentDialog(context, model)
                    }
                } else {
                    visibility = View.GONE
                }
            }

        }
    }
}
