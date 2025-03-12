package com.example.socialmediaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.CommentWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.remote.CommentRemoteDatabase
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.MODIFIED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val commentRemoteDatabase: CommentRemoteDatabase,
    application: Application
) : AndroidViewModel(application) {

    private val _comments = MutableStateFlow(emptyList<Comment>())
    val comment: StateFlow<List<Comment>> = _comments

    private val _commentLivData = MutableLiveData<List<Comment>>()
    val commentLiveData: LiveData<List<Comment>> = _commentLivData

    fun addComment(user: User, postOwnerId: String, postId: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            commentRemoteDatabase.addComment(user, postOwnerId, postId, content)
        }

    }

    fun getCommentsRealtimeUpdates(postOwnerId: String, postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            commentRemoteDatabase.getCommentsRealtimeUpdates(postOwnerId, postId) {
                _comments.value = it
            }
        }
    }

    fun getCommentsRealtimeUpdatesLiveData(postOwnerId: String, postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            commentRemoteDatabase.getCommentsRealtimeUpdates(postOwnerId, postId) {
                _commentLivData.postValue(it)
            }
        }
    }
}