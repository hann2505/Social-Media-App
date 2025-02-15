package com.example.socialmediaapp.data.room.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser

@Dao
interface PostDao {

    @Upsert
    suspend fun upsertAllPosts(posts: List<Post>)

    @Upsert
    suspend fun upsertPost(post: Post)

    @Delete
    suspend fun deletePost(post: Post)

    @Query("SELECT * FROM Post")
    fun getAllPosts(): LiveData<List<Post>>

    @Query("SELECT * FROM Post WHERE postId = :postId")
    fun getPostById(postId: String): LiveData<Post>

    @Query("SELECT * FROM Post WHERE userId = :userId")
    fun getPostsByUserId(userId: String): LiveData<List<Post>>

    @Query(" SELECT post.postId, user.username, user.profilePictureUrl, post.content, post.mediaUrl, post.timestamp\n" +
            "FROM post\n" +
            "INNER JOIN user ON post.userId = user.userId\n" +
            "WHERE post.userId = :userId"
    )
    fun getPostWithUserByUserId(userId: String): LiveData<List<PostWithUser>>


    @Query(" SELECT post.postId, user.username, user.profilePictureUrl, post.content, post.mediaUrl, post.timestamp\n" +
            "FROM post\n" +
            "INNER JOIN user ON post.userId = user.userId\n" +
            "WHERE user.username LIKE '%' || :query || '%' OR post.content LIKE '%' || :query || '%'"
    )
    fun getPostWithUserByText(query: String): LiveData<List<PostWithUser>>



}