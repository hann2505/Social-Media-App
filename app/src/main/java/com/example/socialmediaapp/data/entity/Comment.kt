package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Comment",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Post::class,
            parentColumns = ["postId"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]

)
data class Comment(
    @PrimaryKey(autoGenerate = false)
    val commentId: String = "",
    val userId: String = "",
    val postId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
