package com.example.socialmediaapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.databinding.UserBinding

class UserAdapter(

) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList = listOf<User>()

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }

    class UserViewHolder(
        private val binding: UserBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(user: User) {
            binding.username.text = user.username
            Glide.with(binding.userPfp).load(user.profilePictureUrl).into(binding.userPfp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.bindData(currentUser)

        holder.itemView.setOnClickListener {
            Log.d("onclick user", "$currentUser")
            onItemClickListener?.let {
                it(currentUser)
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setData(userList: List<User>) {
        this.userList = userList
        notifyDataSetChanged()
    }

}


