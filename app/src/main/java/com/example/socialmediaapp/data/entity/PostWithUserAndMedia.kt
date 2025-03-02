package com.example.socialmediaapp.data.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

data class PostWithUserAndMedia(
    @Embedded
    val post: Post,

    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val user: User,

    @Relation(
        parentColumn = "postId",
        entityColumn = "postId",
        entity = PostMedia::class
    )
    val media: List<PostMedia> = emptyList(), // 1 post has many media

    @Relation(
        parentColumn = "postId",
        entityColumn = "postId"
    )
    val postLike: List<PostLike> = emptyList(), // 1 post has many likes

    @Relation(
        parentColumn = "postId",
        entityColumn = "postId"
    )
    val comment: List<Comment> = emptyList() // 1 post has many comments
)
