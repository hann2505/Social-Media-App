package com.example.socialmediaapp.data.entity.post

import androidx.room.PrimaryKey

data class Post (
    @PrimaryKey(autoGenerate = false)
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val listMediaUrls: List<String> = emptyList(),
    val mediaType: MediaType = MediaType.TEXT,
    val postState: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
)

enum class MediaType {
    TEXT,
    IMAGE,
    VIDEO
}
