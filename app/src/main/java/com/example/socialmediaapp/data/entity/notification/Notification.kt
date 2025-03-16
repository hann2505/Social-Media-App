package com.example.socialmediaapp.data.entity.notification

data class Notification(
    val userId: String,
    val postId: String,
    val username: String,
    val profilePictureUrl: Int,
    val timestamp: Long
)
