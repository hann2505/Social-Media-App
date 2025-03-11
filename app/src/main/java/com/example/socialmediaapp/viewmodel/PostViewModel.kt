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

//    private val readAllDatabase: LiveData<List<Post>>
//    private val postRepository: PostRepository
//    private val postMediaRepository: PostMediaRepository

    private val _posts = MutableLiveData<List<PostWithUser>>()
    val posts: LiveData<List<PostWithUser>> = _posts

    private val _searchPosts = MutableLiveData<List<PostWithUser>>()
    val searchPosts: LiveData<List<PostWithUser>> = _searchPosts

//    init {
//        val postDao = AppDatabase.getInstance(application).postDao()
//        val postMediaDao = AppDatabase.getInstance(application).postMediaDao()
//        postRepository = PostRepository(postDao)
//        postMediaRepository = PostMediaRepository(postMediaDao)
//        readAllDatabase = postRepository.readAllDatabase
//    }

    fun fetchPostByUserId(userId: String): LiveData<List<PostWithUser>> {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postRemoteDatabase.getPostByUserIdFromFirebase(userId)
            _posts.postValue(posts)
        }
        return posts
    }

    fun getPostWithUserRealtime(userId: String): LiveData<List<PostWithUser>> {
        val postsLiveData = MutableLiveData<List<PostWithUser>>()
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.getPostWithUserRealTime(userId) { posts ->
                postsLiveData.postValue(posts)
            }
        }
        return postsLiveData
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

    fun searchPostFromFirebase(userId: String): LiveData<List<PostWithUser>> {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postRemoteDatabase.getPostByUserIdFromFirebase(userId)
            _searchPosts.postValue(posts)
        }
        return searchPosts
    }

    private fun getPostChangesOnceFromFirebase(): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.postChangesOnce { changeType, post ->
                viewModelScope.launch(Dispatchers.IO) {
                    when (changeType) {
                        ADDED, MODIFIED -> {

                        }
                        REMOVED -> {

                        }
                        else -> {}
                    }
                }
            }
        }
    }

    private fun getPostMediaChangeOnceFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.postMediaChangesOnce { changeType, post ->
                viewModelScope.launch(Dispatchers.IO) {
                    when (changeType) {
                        ADDED, MODIFIED -> {

                        }
                        REMOVED -> {

                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun checkIfPostChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.listenForPostChanges { changeType, post ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("post", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            Log.d("post", "added: $post")
                        }

                        REMOVED -> {
                            Log.d("post", "removed: $post")
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun fetchPostFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            getPostChangesOnceFromFirebase().join()
        }
    }

    fun checkIfPostMediaChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.listenForPostMediaChanges { changeType, postMedia ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("post", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            Log.d("post", "added: $postMedia")
                        }

                        REMOVED -> {
                            Log.d("post", "removed: $postMedia")
                        }

                        else -> {}
                    }
                }
            }
        }
    }


}