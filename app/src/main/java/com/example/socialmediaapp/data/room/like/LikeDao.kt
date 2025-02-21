package com.example.socialmediaapp.data.room.like

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
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

    @Query("SELECT EXISTS(SELECT 1 FROM postlike WHERE userId = :userId AND postId = :postId)")
    fun checkIfLiked(userId: String, postId: String): LiveData<Boolean>

    @Query("SELECT * FROM postlike WHERE postId = :postId")
    fun getPostLikesByPostId(postId: String): LiveData<List<PostLike>>

    @Query("SELECT postId FROM postlike WHERE userId = :postId")
    fun getPostIdByUserIds(postId: String): LiveData<List<String>>

    @Query("SELECT COUNT(*) FROM postlike WHERE postId = :postId")
    fun getPostLikeCount(postId: String): LiveData<Int>
}