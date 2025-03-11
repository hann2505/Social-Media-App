package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

data class Comment(
    @PrimaryKey(autoGenerate = false)
    val commentId: String = "",
    val userId: String = "",
    val postId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
