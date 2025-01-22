package com.example.socialmediaapp.data.entity

data class Post (
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val mediaType: String = "",
    val mediaUrl: String = "",
    val postState: Boolean = true,
    val timestamp: Long = 0,
)