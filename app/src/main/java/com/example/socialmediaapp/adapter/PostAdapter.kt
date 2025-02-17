package com.example.socialmediaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.databinding.PostBinding

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList = listOf<PostWithUser>()

    private var onCommentClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnCommentClickListener(listener: (PostWithUser) -> Unit) {
        onCommentClickListener = listener
    }

    class PostViewHolder(
        private val binding: PostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: PostWithUser) {
            binding.userName.text = post.username
            binding.caption.text = post.content
            Glide.with(binding.userPfp).load(post.profilePictureUrl).into(binding.userPfp)
            Glide.with(binding.image).load(post.mediaUrl).into(binding.image)
        }

        fun setOnCommentClickListener(listener: () -> Unit) {
            binding.commentLayout.setOnClickListener {
                listener()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]
        holder.bindData(currentPost)

        holder.setOnCommentClickListener {
            onCommentClickListener?.invoke(currentPost)
        }
    }

    fun setData(postList: List<PostWithUser>) {
        this.postList = postList
        notifyDataSetChanged()
    }
}