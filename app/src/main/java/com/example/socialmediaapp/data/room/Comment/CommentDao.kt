package com.example.socialmediaapp.data.room.Comment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.CommentWithUser

@Dao
interface CommentDao {

    @Upsert
    suspend fun upsertComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)

    @Query("SELECT * FROM Comment")
    fun getAllComments(): LiveData<List<Comment>>

    @Query(
        "SELECT comment.commentId, post.postId, user.username, user.profilePictureUrl, comment.content, comment.timestamp FROM Comment\n" +
        "INNER JOIN User ON comment.userId = user.userId\n" +
        "INNER JOIN post ON post.postId = comment.postId\n" +
        "WHERE post.postId = :postId "
    )
    fun getCommentWithUserByUserId(postId: String): LiveData<List<CommentWithUser>>

}