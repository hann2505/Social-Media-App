package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.firebase.remote.PostRemoteDatabase
import com.example.socialmediaapp.other.FirebaseChangeType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel@Inject constructor(
    application: Application,
    private val postRemoteDatabase: PostRemoteDatabase
) : AndroidViewModel(application) {

    private val _posts = MutableLiveData<List<PostWithUser>>()
    val posts: LiveData<List<PostWithUser>> = _posts

    private val _searchPosts = MutableLiveData<List<PostWithUser>>()
    val searchPosts: LiveData<List<PostWithUser>> = _searchPosts

    fun fetchPostByUserId(userId: String): LiveData<List<PostWithUser>> {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postRemoteDatabase.getPostByUserIdFromFirebase(userId)
            _posts.postValue(posts)
        }
        return posts
    }

    fun fetchPostRealtimeUpdate(userId: String): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.getPostRealtimeUpdate(userId) { posts ->
                _posts.postValue(posts)
            }
        }
    }

    fun uploadPost(
        userId: String,
        content: String,
        imageUrl: List<Uri>,
        postState: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.handleImageUpload(
                userId = userId,
                content = content,
                imageUri = imageUrl,
                postState = postState
            )
        }
    }

    fun searchPostFromFirebaseByContentOrUsername(query: String): LiveData<List<PostWithUser>> {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postRemoteDatabase.searchPostsByContentOrUsername(query)
            _searchPosts.postValue(posts)
        }
        return searchPosts
    }



}