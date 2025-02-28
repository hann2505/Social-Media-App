package com.example.socialmediaapp.data.entity

import androidx.room.Embedded
import androidx.room.Relation

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
        entityColumn = "postId"
    )
    val media: PostMedia
)
