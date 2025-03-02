package com.example.socialmediaapp.data.room.post

import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia

class PostRepository(
    private val postDao: PostDao
) {
    val readAllDatabase: LiveData<List<Post>> = postDao.getAllPosts()

    suspend fun upsertAllPosts(posts: List<Post>) {
        postDao.upsertAllPosts(posts)
    }

    suspend fun upsertPost(post: Post) {
        postDao.upsertPost(post)
    }

    suspend fun deletePost(post: Post) {
        postDao.deletePost(post)
    }

    fun getPostWithUserByUserId(userId: String): LiveData<List<PostWithUser>>{
        return postDao.getPostWithUserByUserId(userId)
    }

    fun getPostWithUserByText(query: String): LiveData<List<PostWithUser>>{
        return postDao.getPostWithUserByText(query)
    }

    fun getPostWithUserAndMedias(userId: String): LiveData<List<PostWithUserAndMedia>> {
        return postDao.getAllPostsWithUserAndMedias(userId)
    }

    fun getPostWithUserAndMediasByQuery(query: String): LiveData<List<PostWithUserAndMedia>> {
        return postDao.getPostWithUserAndMediasByQuery(query)

    }

}