package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "CommentLike",
    foreignKeys = [
            ForeignKey(
                entity = Comment::class,
                parentColumns = ["commentId"],
                childColumns = ["commentId"],
                onDelete = ForeignKey.CASCADE
            )
    ]
)
data class CommentLike(
    @PrimaryKey(autoGenerate = false)
    val likeId: String = "",
    val userId: String = "",
    val commentId: String = "",
    val timestamp: Long = 0
)
