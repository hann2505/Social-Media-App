package com.example.socialmediaapp.data.entity.like

data class CommentLike(
    val likeId: String = "",
    val userId: String = "",
    val commentId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
