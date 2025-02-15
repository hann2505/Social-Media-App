package com.example.socialmediaapp.data.room.Comment

import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.Comment

class CommentRepository(
    private val commentDao: CommentDao
) {
//    val readAllDatabase: LiveData<List<Comment>> = commentDao.getAllComments()

    suspend fun upsertComments(comments: Comment) {
        commentDao.upsertComment(comments)
    }

    suspend fun deleteComment(comment: Comment) {
        commentDao.deleteComment(comment)
    }

}