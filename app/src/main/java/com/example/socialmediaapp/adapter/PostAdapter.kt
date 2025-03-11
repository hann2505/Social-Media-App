package com.example.socialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia
import com.example.socialmediaapp.databinding.PostBinding
import com.example.socialmediaapp.extensions.TimeConverter

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private val adapter = PostImageViewPagerAdapter()

    private var postList = listOf<PostWithUser>()
    private var likedList = listOf<String>()

    private var isNavigatedByNotification = false

    private var onCommentClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnCommentClickListener(listener: (PostWithUser) -> Unit) {
        onCommentClickListener = listener
    }

    private var onLikeClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnLikeClickListener(listener: (PostWithUser) -> Unit) {
        onLikeClickListener = listener
    }

    private var onBackClickListener: (() -> Unit)? = null

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClickListener = listener
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

            if (isNavigatedByNotification)
                binding.backBtn.visibility = View.VISIBLE
        }

        fun setUpRecyclerView(post: PostWithUser) {
            post.mediaUrls.map {
                it.toUri()
            }.let {
                Log.d("PostAdapter", "setUpRecyclerView: $it")
                adapter.updateList(it)
            }
            binding.viewPager.adapter = adapter
            binding.wormDot.attachTo(binding.viewPager)
        }

        fun setOnCommentClickListener(listener: () -> Unit) {
            binding.commentLayout.setOnClickListener {
                listener()
            }
        }

        fun setOnLikeClickListener(listener: () -> Unit) {
            binding.likeBtn.setOnClickListener {
                listener()
            }
        }

        fun setOnBackClickListener(listener: () -> Unit) {
            binding.backBtn.setOnClickListener {
                listener()
            }
        }

        private fun changeLikeButton(post: PostWithUser) {
            val isLiked = likedList.contains(post.postId)
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

        holder.setOnLikeClickListener {
            onLikeClickListener?.invoke(currentPost)
        }

        holder.setOnBackClickListener {
            onBackClickListener?.invoke()
        }

        holder.setUpRecyclerView(currentPost)
    }

    fun setData(postList: List<PostWithUser>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    fun setLikedList(likedList: List<String>) {
        this.likedList = likedList
        notifyDataSetChanged()
    }

    fun setIsNavigatedByNotification() {
        isNavigatedByNotification = true
    }
}