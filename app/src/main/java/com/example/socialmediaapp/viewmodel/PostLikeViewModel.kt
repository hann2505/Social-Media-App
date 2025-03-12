package com.example.socialmediaapp.viewmodel

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.firebase.remote.PostLikeRemoteFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostLikeViewModel @Inject constructor(
    private val postLikeRemoteDatabase: PostLikeRemoteFirebase
): ViewModel() {

    private val _likeCount = MutableStateFlow(0)
    val likeCount: StateFlow<Int> = _likeCount

    fun addPostLike(currentUserId: String, userId: String, postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteDatabase.addPostLike(currentUserId, userId, postId)
        }
    }

    fun deletePostLike(currentUserId: String, userId: String, postId: String) {
        viewModelScope.launch (Dispatchers.IO) {
            postLikeRemoteDatabase.deletePostLike(currentUserId, userId, postId)
        }
    }

    fun observeLikeCount(postOwnerId: String, postId: String): StateFlow<Int> {
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteDatabase.observeLikeCount(postOwnerId, postId) {
                _likeCount.value = it
            }
        }
        return likeCount
    }
}