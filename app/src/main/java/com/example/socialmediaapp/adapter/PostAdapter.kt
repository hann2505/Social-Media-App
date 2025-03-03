package com.example.socialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia
import com.example.socialmediaapp.databinding.PostBinding
import com.example.socialmediaapp.extensions.TimeConverter

class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList = listOf<PostWithUserAndMedia>()
    private var likedList = listOf<String>()

    private var onCommentClickListener: ((PostWithUserAndMedia) -> Unit)? = null

    fun setOnCommentClickListener(listener: (PostWithUserAndMedia) -> Unit) {
        onCommentClickListener = listener
    }

    private var onLikeClickListener: ((PostWithUserAndMedia) -> Unit)? = null

    fun setOnLikeClickListener(listener: (PostWithUserAndMedia) -> Unit) {
        onLikeClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: PostWithUserAndMedia) {
            binding.userName.text = post.user.username
            binding.caption.text = post.post.content
            binding.likeCount.text = post.postLike.size.toString()
            binding.commentCount.text = post.comment.size.toString()
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(post.post.timestamp)
            changeLikeButton(post)
            Glide.with(binding.userPfp).load(post.user.profilePictureUrl).into(binding.userPfp)

        }

        fun setUpRecyclerView(post: PostWithUserAndMedia) {
            val adapter = PostImageViewPagerAdapter()
            post.media.map {
                it.mediaUrl.toUri()
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

        private fun changeLikeButton(postWithUserAndMedia: PostWithUserAndMedia) {
            val isLiked = likedList.contains(postWithUserAndMedia.post.postId)
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

        holder.setUpRecyclerView(currentPost)
    }

    fun setData(postList: List<PostWithUserAndMedia>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    fun setLikedList(likedList: List<String>) {
        this.likedList = likedList
        notifyDataSetChanged()
    }
}