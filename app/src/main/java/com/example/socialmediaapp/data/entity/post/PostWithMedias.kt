package com.example.socialmediaapp.data.entity.post

import androidx.room.Embedded
import androidx.room.Relation

data class PostWithMedias(
    @Embedded
    val post: Post,
    @Relation(
        parentColumn = "postId",
        entityColumn = "postId"
    )
    val medias: List<PostMedia>
)
