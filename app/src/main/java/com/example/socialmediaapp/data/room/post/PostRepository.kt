package com.example.socialmediaapp.data.room.post

import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser

class PostRepository(
    private val postDao: PostDao
) {
    val readAllDatabase: LiveData<List<Post>> = postDao.getAllPosts()

    suspend fun upsertAllPosts(posts: List<Post>) {
        postDao.upsertAllPosts(posts)
    }

    fun getPostWithUserByUserId(userId: String): LiveData<List<PostWithUser>>{
        return postDao.getPostWithUserByUserId(userId)
    }

}