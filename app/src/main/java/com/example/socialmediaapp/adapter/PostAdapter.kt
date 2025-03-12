package com.example.socialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.firebase.remote.CommentRemoteDatabase
import com.example.socialmediaapp.data.firebase.remote.PostLikeRemoteFirebase
import com.example.socialmediaapp.databinding.PostBinding
import com.example.socialmediaapp.extensions.TimeConverter
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class PostAdapter @Inject constructor(
    private val postLikeRemoteFirebase: PostLikeRemoteFirebase,
    private val commentRemoteFirebase: CommentRemoteDatabase,
    private val auth: FirebaseAuth
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var postList = listOf<PostWithUser>()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private var isNavigatedByNotification = false

    private var onCommentClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnCommentClickListener(listener: (PostWithUser) -> Unit) {
        onCommentClickListener = listener
    }

    private var onItemClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnItemClickListener(listener: (PostWithUser) -> Unit) {
        onItemClickListener = listener
    }

    private var onBackClickListener: (() -> Unit)? = null

    fun setOnBackClickListener(listener: () -> Unit) {
        onBackClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        val adapter = PostImageViewPagerAdapter()

        fun bindData(post: PostWithUser) {
            postLikeRemoteFirebase.observeLikeCount(post.userId, post.postId) {
                binding.likeCount.text = it.toString()
            }

            commentRemoteFirebase.observeCommentCount(post.userId, post.postId) {
                binding.commentCount.text = it.toString()
            }

            binding.userName.text = post.username
            binding.caption.text = post.content
            binding.commentCount.text = post.commentCount.toString()
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(post.timestamp)
            Glide.with(binding.userPfp).load(post.profilePictureUrl).into(binding.userPfp)

            if (isNavigatedByNotification)
                binding.backBtn.visibility = View.VISIBLE
        }

        fun setUpRecyclerView(post: PostWithUser) {

            adapter.updateList(emptyList())

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

        fun setOnLikeClickListener(post: PostWithUser) {
            binding.likeBtn.setOnClickListener {
                postLikeRemoteFirebase.isLiked(currentUserId, post.userId, post.postId) {
                    if (it) {
                        postLikeRemoteFirebase.deletePostLike(currentUserId, post.userId, post.postId)
                        binding.likeBtn.setBackgroundResource(R.drawable.ic_heart)
                    }
                    else {
                        postLikeRemoteFirebase.addPostLike(currentUserId, post.userId, post.postId)
                        binding.likeBtn.setBackgroundResource(R.drawable.ic_hearted)
                    }
                }

            }
        }

        fun setOnBackClickListener(listener: () -> Unit) {
            binding.backBtn.setOnClickListener {
                listener()
            }
        }

        fun checkIfLiked(post: PostWithUser) {
            postLikeRemoteFirebase.isLiked(currentUserId, post.userId, post.postId) {
                if (it) {
                    binding.likeBtn.setBackgroundResource(R.drawable.ic_hearted)
                }
                else {
                    binding.likeBtn.setBackgroundResource(R.drawable.ic_heart)
                }
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
        holder.setOnLikeClickListener(currentPost)

        holder.setOnBackClickListener {
            onBackClickListener?.invoke()
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(currentPost)
            }
        }

        holder.setUpRecyclerView(currentPost)

        holder.checkIfLiked(currentPost)
    }

    fun setData(postList: List<PostWithUser>) {
        this.postList = postList
        notifyDataSetChanged()
    }

    fun setIsNavigatedByNotification() {
        isNavigatedByNotification = true
    }
}