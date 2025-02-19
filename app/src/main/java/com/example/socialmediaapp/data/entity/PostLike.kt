package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "PostLike",
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["postId"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostLike(
    @PrimaryKey(autoGenerate = false)
    val likeId: String = "",
    val userId: String = "",
    val postId: String = "",
    val timestamp: Long = 0
)
