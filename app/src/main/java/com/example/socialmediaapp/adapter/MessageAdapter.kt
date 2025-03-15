package com.example.socialmediaapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.data.entity.Message
import com.example.socialmediaapp.databinding.ChatBoxLeftBinding
import com.example.socialmediaapp.databinding.ChatBoxRightBinding
import com.example.socialmediaapp.extensions.TimeConverter
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class MessageAdapter @Inject constructor(
    auth: FirebaseAuth
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messageList = mutableListOf<Message>()
    private val curUserId = auth.currentUser?.uid

    companion object {
        const val VIEW_TYPE_MESSAGE_SENT = 1
        const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    inner class SentMessageHolder(private val binding: ChatBoxRightBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData (message: Message) {
            binding.message.text = message.content
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(message.timestamp)
        }
    }

    inner class ReceivedMessageHolder(private val binding: ChatBoxLeftBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(message: Message) {
            binding.message.text = message.content
            binding.timestamp.text = TimeConverter.convertTimestampToDateTime(message.timestamp)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT ->
                SentMessageHolder(ChatBoxRightBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            VIEW_TYPE_MESSAGE_RECEIVED ->
                ReceivedMessageHolder(ChatBoxLeftBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageHolder -> holder.bindData(messageList[position])
            is ReceivedMessageHolder -> holder.bindData(messageList[position])

        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == curUserId) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    fun updateMessageList(message: List<Message>) {
        messageList.clear()
        messageList.addAll(message)
        notifyDataSetChanged()
    }
}