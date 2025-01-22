package com.example.socialmediaapp.data.entity

data class Comment(
    val commentId: String = "",
    val userId: String = "",
    val postId: String = "",
    val content: String = "",
    val timestamp: Long = 0
)
