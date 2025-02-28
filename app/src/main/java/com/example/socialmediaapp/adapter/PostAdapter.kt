package com.example.socialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.databinding.PostBinding
import com.example.socialmediaapp.extensions.TimeConverter

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList = listOf<PostWithUser>()
    private var likedList = listOf<String>()

    private var onCommentClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnCommentClickListener(listener: (PostWithUser) -> Unit) {
        onCommentClickListener = listener
    }

    private var onLikeClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnLikeClickListener(listener: (PostWithUser) -> Unit) {
        onLikeClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: PostWithUser) {
            binding.userName.text = post.username
            binding.caption.text = post.content
            binding.likeCount.text = post.likeCount.toString()
            binding.commentCount.text = post.commentCount.toString()
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(post.timestamp)
            changeLikeButton(post)
            Glide.with(binding.userPfp).load(post.profilePictureUrl).into(binding.userPfp)

        }

        private fun setUpRecyclerView() {
//            val adapter = PostImageAdapter(post.imageUrls)
//            binding.viewPager.adapter = adapter
        }

        fun setOnCommentClickListener(listener: () -> Unit) {
            binding.commentLayout.setOnClickListener {
                listener()
            }
        }

        fun setOnLikeClickListener(postWithUser: PostWithUser, listener: () -> Unit) {
            binding.likeBtn.setOnClickListener {
                listener()
            }
        }

        private fun changeLikeButton(postWithUser: PostWithUser) {
            val isLiked = likedList.contains(postWithUser.postId)
            if (isLiked) {
                binding.likeBtn.setBackgroundResource(R.drawable.ic_hearted)
            }
            else {
                binding.likeBtn.setBackgroundResource(R.drawable.ic_heart)
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

        holder.setOnLikeClickListener(currentPost) {
            onLikeClickListener?.invoke(currentPost)
        }
    }

    fun setData(postList: List<PostWithUser>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    fun setLikedList(likedList: List<String>) {
        this.likedList = likedList
        notifyDataSetChanged()
    }
}