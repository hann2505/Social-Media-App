package com.example.socialmediaapp.data.room.Comment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.Comment

@Dao
interface CommentDao {

    @Upsert
    suspend fun upsertComment(comment: Comment)

    @Delete
    suspend fun deleteComment(comment: Comment)

//    @Query("SELECT * FROM Comment")
//    fun getAllComments(): LiveData<List<Comment>>

}