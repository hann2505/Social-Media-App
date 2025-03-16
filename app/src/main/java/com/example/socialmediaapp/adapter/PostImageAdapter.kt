package com.example.socialmediaapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.databinding.PostImageBinding

class PostImageAdapter(
) : RecyclerView.Adapter<PostImageAdapter.PostViewHolder>() {

    private val imageUrls = mutableListOf<Uri>()

    private var onCancelClickListener: ((Uri) -> Unit)? = null

    fun setOnCancelClickListener(listener: (Uri) -> Unit) {
        onCancelClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: PostImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image

        fun setOnCancelClickListener(listener: () -> Unit) {
            binding.cancel.setOnClickListener {
                listener()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = imageUrls.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Glide.with(holder.image.context)
            .load(imageUrls[position].toString())
            .into(holder.image)

        holder.setOnCancelClickListener {
            onCancelClickListener?.invoke(imageUrls[position])
        }
    }

    fun updateList(newList: List<Uri>) {
        imageUrls.clear()
        imageUrls.addAll(newList)
        notifyDataSetChanged()
    }
}