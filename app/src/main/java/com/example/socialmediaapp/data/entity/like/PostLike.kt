package com.example.socialmediaapp.data.entity.like

import androidx.room.PrimaryKey

data class PostLike(
    @PrimaryKey(autoGenerate = false)
    val likeId: String = "",
    val userId: String = "",
    val postId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
