package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class CommentLike(
    val likeId: String = "",
    val userId: String = "",
    val commentId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
