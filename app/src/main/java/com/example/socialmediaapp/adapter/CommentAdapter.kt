package com.example.socialmediaapp.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.CommentWithUser
import com.example.socialmediaapp.databinding.CommentBinding

class CommentAdapter: RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val commentList = mutableListOf<CommentWithUser>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {

        return CommentViewHolder(
            CommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val currentComment = commentList[position]
        holder.bindData(currentComment)
    }

    override fun getItemCount(): Int = commentList.size

    inner class CommentViewHolder(private val binding: CommentBinding) : RecyclerView.ViewHolder(binding.root)  {
        fun bindData(comment: CommentWithUser) {
            binding.userComment.text = comment.content
            binding.userName.text = comment.username
            Glide.with(binding.userPfp).load(comment.profilePictureUrl).into(binding.userPfp)
        }
    }

    fun setData(commentList: List<CommentWithUser>) {
        this.commentList.clear()
        this.commentList.addAll(commentList)
        notifyDataSetChanged()
    }
}
