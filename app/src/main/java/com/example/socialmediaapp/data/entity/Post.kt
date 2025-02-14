package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Post",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Post (
    @PrimaryKey(autoGenerate = false)
    val postId: String = "",
    val userId: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val mediaType: MediaType = MediaType.TEXT,
    val mediaUrl: String = "",
    val postState: Boolean = true,
    val timestamp: Long = 0,
)

enum class MediaType {
    TEXT,
    IMAGE,
    VIDEO
}
