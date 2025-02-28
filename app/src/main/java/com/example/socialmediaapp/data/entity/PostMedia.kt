package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Post Medias",
    foreignKeys = [
        ForeignKey(
            entity = Post::class,
            parentColumns = ["postId"],
            childColumns = ["postId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostMedia(
    @PrimaryKey(autoGenerate = false)
    val imageId: String = "",
    val postId: String = ""
)
