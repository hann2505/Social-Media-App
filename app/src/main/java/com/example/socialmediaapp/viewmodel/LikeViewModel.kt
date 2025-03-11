package com.example.socialmediaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.firebase.remote.PostLikeRemoteFirebase
import com.example.socialmediaapp.other.FirebaseChangeType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    application: Application,
    private val postLikeRemoteFirebase: PostLikeRemoteFirebase
) : AndroidViewModel(application) {

    fun likePost(userId: String, postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteFirebase.addPostLike(userId, postId)
        }
    }

    fun unlikePost(userId: String, postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteFirebase.deletePostLike(userId, postId)
        }
    }

    fun isLiked(userId: String, postId: String): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            val isLiked = postLikeRemoteFirebase.isLiked(userId, postId)
            result.postValue(isLiked)
        }
        return result
    }

    fun getLikeCount(postId: String): LiveData<Int> {
        val result = MutableLiveData<Int>()
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteFirebase.getLikeCountRealtime(postId) {
                result.postValue(it)
            }
        }
        return result
    }

    fun getLikedPostByCurrentUser(userId: String): LiveData<List<String>> {
        val result = MutableLiveData<List<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            val postLikes = postLikeRemoteFirebase.getLikedPostByCurrentUser(userId)
            result.postValue(postLikes)
        }
        return result
    }

    fun checkIfLikeChanges(userId: String): LiveData<List<String>> {
        val result = MutableLiveData<List<String>>()
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteFirebase.listenToPostLikesChange(userId) { likePostIds ->
                result.postValue(likePostIds)
            }
        }
        return result
    }
}