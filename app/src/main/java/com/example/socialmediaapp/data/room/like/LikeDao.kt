package com.example.socialmediaapp.data.room.like

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.CommentLike
import com.example.socialmediaapp.data.entity.PostLike

@Dao
interface LikeDao {

    @Upsert
    suspend fun addPostLike(like: PostLike)

    @Upsert
    suspend fun addCommentLike(like: CommentLike)

    @Delete
    suspend fun deletePostLike(like: PostLike)

    @Delete
    suspend fun deleteCommentLike(like: CommentLike)


}