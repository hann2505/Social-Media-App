package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.firebase.remote.PostRemoteDatabase
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.data.room.post.PostRepository
import com.example.socialmediaapp.other.FirebaseChangeType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel@Inject constructor(
    application: Application,
    private val postRemoteDatabase: PostRemoteDatabase
) : AndroidViewModel(application) {

    private val readAllDatabase: LiveData<List<Post>>
    private val postRepository: PostRepository

    init {
        val postDao = AppDatabase.getInstance(application).postDao()
        postRepository = PostRepository(postDao)
        readAllDatabase = postRepository.readAllDatabase
    }

    fun fetchDataFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val posts = postRemoteDatabase.getAllPost()
            for (post in posts) {
                Log.d("post view model", "$post")
            }

            postRepository.upsertAllPosts(posts)
        }
    }

    fun uploadPost(
        userId: String,
        content: String,
        imageUrl: String,
        mediaUrl: String,
        postState: Boolean,
        timestamp: Long
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.uploadPost(
                userId,
                content,
                imageUrl,
                mediaUrl,
                postState,
                timestamp
            )
        }
    }

    fun getPostWithUserByUserId(userId: String): LiveData<List<PostWithUser>> {
        return postRepository.getPostWithUserByUserId(userId)
    }

    fun checkIfPostChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.listenForPostChanges { changeType, post ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("post", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            postRepository.upsertPost(post)
                            Log.d("post", "added: $post")
                        }

                        REMOVED -> {
                            Log.d("post", "removed: $post")
                            postRepository.deletePost(post)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

}