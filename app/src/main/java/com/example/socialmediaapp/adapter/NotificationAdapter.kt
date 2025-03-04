package com.example.socialmediaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.Notification
import com.example.socialmediaapp.databinding.NotificationBinding
import com.example.socialmediaapp.extensions.TimeConverter

class NotificationAdapter(
) : RecyclerView.Adapter<NotificationAdapter.PostViewHolder>() {

    private val notifications = mutableListOf<Notification>()

    private var onCancelClickListener: ((Notification) -> Unit)? = null

    fun setOnCancelClickListener(listener: (Notification) -> Unit) {
        onCancelClickListener = listener
    }

    inner class PostViewHolder(
        private val binding: NotificationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(notification: Notification) {
            binding.notificationTv.text = binding.root.context.getString(R.string.notification_sample, notification.username)
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(notification.timestamp)
        }

        fun setOnCancelClickListener(listener: () -> Unit) {

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

        holder.setOnCancelClickListener {

        }
    }

    fun updateList(newList: List<Notification>) {
        notifications.clear()
        notifications.addAll(newList)
        notifyDataSetChanged()
    }
}