package com.example.socialmediaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.databinding.PostBinding
import com.example.socialmediaapp.databinding.UserBinding

class SearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Any>()

    private var onUserItemClickListener: ((User) -> Unit)? = null

    fun setOnUserItemClickListener(listener: (User) -> Unit) {
        onUserItemClickListener = listener
    }

    private var onPostItemClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnPostItemClickListener(listener: (PostWithUser) -> Unit) {
        onPostItemClickListener = listener
    }

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_POST = 1
    }

    inner class UserViewHolder(private val binding: UserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(user: User) {
            binding.username.text = user.username
            Glide.with(binding.userPfp).load(user.profilePictureUrl).into(binding.userPfp)
        }
    }

    inner class PostViewHolder(private val binding: PostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(post: PostWithUser) {
            binding.userName.text = post.username
            binding.caption.text = post.content
            Glide.with(binding.userPfp).load(post.profilePictureUrl).into(binding.userPfp)
            Glide.with(binding.image).load(post.mediaUrl).into(binding.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_USER ->
                UserViewHolder(UserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            TYPE_POST ->
                PostViewHolder(PostBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder) {
            is UserViewHolder -> holder.bindData(items[position] as User)
            is PostViewHolder -> holder.bindData(items[position] as PostWithUser)
        }

        holder.itemView.setOnClickListener {
            when (holder) {
                is UserViewHolder ->
                    onUserItemClickListener?.let {
                        it(items[position] as User)
                    }
                is PostViewHolder ->
                    onPostItemClickListener?.let {
                        it(items[position] as PostWithUser)
                    }
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (items[position] is User) {
            TYPE_USER
        } else {
            TYPE_POST
        }
    }

    fun setData(newItems: List<Any>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}