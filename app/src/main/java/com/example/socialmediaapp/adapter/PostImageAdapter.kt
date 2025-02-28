package com.example.socialmediaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.databinding.PostImageBinding

class PostImageAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<PostImageAdapter.PostViewHolder>() {
    inner class PostViewHolder(
        private val binding: PostImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.image
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
            .load(imageUrls[position])
            .into(holder.image)
    }
}