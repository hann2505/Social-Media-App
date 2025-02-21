package com.example.socialmediaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.data.firebase.remote.PostLikeRemoteFirebase
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.data.room.like.post.PostLikeRepository
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    application: Application,
    private val postLikeRemoteFirebase: PostLikeRemoteFirebase
) : AndroidViewModel(application) {

    private val postRepository: PostLikeRepository

    init {
        val likeDao = AppDatabase.getInstance(application).likeDao()
        postRepository = PostLikeRepository(likeDao)
    }

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

    fun checkIfLiked(userId: String, postId: String): LiveData<Boolean> {
        return postRepository.checkIfLiked(userId, postId)
    }

    fun getLikesByPostId(postId: String): LiveData<List<PostLike>> {
        return postRepository.getLikesByPostId(postId)
    }

    fun getPostIdByUserId(postId: String): LiveData<List<String>> {
        return postRepository.getPostIdByUserId(postId)
    }

    fun getPostLikeCount(postId: String): LiveData<Int> {
        return postRepository.getPostLikeCount(postId)
    }

    fun checkIfLikeChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            postLikeRemoteFirebase.listenToPostLikesChange { firebaseChangeType, postLike ->
                viewModelScope.launch(Dispatchers.IO) {
                    when (firebaseChangeType) {
                        ADDED -> {
                            postRepository.addPostLike(postLike)
                        }
                        REMOVED -> {
                            postRepository.deletePostLike(postLike)
                        }
                        else -> {}
                    }
                }

            }

        }
    }
}