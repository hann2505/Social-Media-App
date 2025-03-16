package com.example.socialmediaapp.adapter.notification

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.post.PostWithUser
import com.example.socialmediaapp.databinding.NotificationBinding
import com.example.socialmediaapp.extensions.TimeConverter

class NotificationAdapter(
) : RecyclerView.Adapter<NotificationAdapter.PostViewHolder>() {

    private val notifications = mutableListOf<PostWithUser>()

    private var onItemClickListener: ((PostWithUser) -> Unit)? = null

    fun setOnItemClickListener(listener: (PostWithUser) -> Unit) {
        onItemClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: NotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(notification: PostWithUser) {
            val text = binding.root.context.getString(R.string.notification_sample, "<b>${notification.username}</b>")
            val styledText = Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            binding.notificationTv.text = styledText
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(notification.timestamp)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = NotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int = notifications.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        holder.bindData(notifications[position])

        holder.itemView.setOnClickListener {
            onItemClickListener?.let {
                it(notifications[position])
            }
        }
    }

    fun updateList(newList: List<PostWithUser>) {
        notifications.clear()
        notifications.addAll(newList)
        notifyDataSetChanged()
    }
}