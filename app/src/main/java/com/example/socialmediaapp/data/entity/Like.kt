package com.example.socialmediaapp.data.entity

data class Like(
    val likeId: String = "",
    val userId: String = "",
    val postId: String = "",
    val commentId: String = "",
    val timestamp: Long = 0
)
