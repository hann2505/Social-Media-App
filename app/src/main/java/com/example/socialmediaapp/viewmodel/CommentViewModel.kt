package com.example.socialmediaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.CommentWithUser
import com.example.socialmediaapp.data.firebase.remote.CommentRemoteDatabase
import com.example.socialmediaapp.data.room.Comment.CommentRepository
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.MODIFIED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRemoteDatabase: CommentRemoteDatabase,
    application: Application
) : AndroidViewModel(application) {

    private val readAllDatabase: LiveData<List<Comment>>
    private val commentRepository: CommentRepository

    init {
        val commentDao = AppDatabase.getInstance(application).commentDao()
        commentRepository = CommentRepository(commentDao)
        readAllDatabase = commentRepository.readAllDatabase
    }

    fun checkIfCommentChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            commentRemoteDatabase.listenForCommentsChanges { changeType, comment ->
                viewModelScope.launch(Dispatchers.IO) {
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            commentRepository.upsertComments(comment)
                        }

                        REMOVED -> {
                            commentRepository.deleteComment(comment)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun addComment(userId: String, postId: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            commentRemoteDatabase.addComment(userId, postId, content)
        }

    }

    fun getCommentWithUser(postId: String): LiveData<List<CommentWithUser>> {
        return commentRepository.getCommentWithUserByUserId(postId)
    }
}