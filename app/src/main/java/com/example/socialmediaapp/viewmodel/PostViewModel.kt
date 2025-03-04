package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Notification
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia
import com.example.socialmediaapp.data.firebase.remote.PostRemoteDatabase
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.data.room.media.PostMediaRepository
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
    private val postMediaRepository: PostMediaRepository

    private val _imageUrl = MutableLiveData<String>()
    val imageUrl: LiveData<String> = _imageUrl

    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _error

    init {
        val postDao = AppDatabase.getInstance(application).postDao()
        val postMediaDao = AppDatabase.getInstance(application).postMediaDao()
        postRepository = PostRepository(postDao)
        postMediaRepository = PostMediaRepository(postMediaDao)
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

    fun checkIfPostMediaChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            postRemoteDatabase.listenForPostMediaChanges { changeType, postMedia ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("post", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            postMediaRepository.upsertPostMedia(postMedia)
                            Log.d("post", "added: $postMedia")
                        }

                        REMOVED -> {
                            Log.d("post", "removed: $postMedia")
                            postMediaRepository.deletePostMedia(postMedia)
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun getPostWithUserByText(username: String): LiveData<List<PostWithUser>> {
        return postRepository.getPostWithUserByText(username)
    }

    fun getPostWithUserAndImage(userId: String): LiveData<List<PostWithUserAndMedia>> {
        return postRepository.getPostWithUserAndMedias(userId)
    }

    fun getPostWithUserAndImageByQuery(query: String): LiveData<List<PostWithUserAndMedia>> {
        return postRepository.getPostWithUserAndMediasByQuery(query)
    }

    fun getNotificationByUserId(userId: String): LiveData<List<Notification>> {
        return postRepository.getNotificationByUserId(userId)
    }

}