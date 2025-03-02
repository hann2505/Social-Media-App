package com.example.socialmediaapp.data.room.post

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia

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

    @Transaction
    @Query("SELECT * FROM Post WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllPostsWithUserAndMedias(userId: String): LiveData<List<PostWithUserAndMedia>>

//    @Transaction
//    @Query("SELECT * FROM Post WHERE postId = :postId ORDER BY timestamp DESC")
//    fun getPostWithUserAndMedias(postId: String): LiveData<List<PostWithMedias>>

    @Transaction
    @Query("SELECT * FROM Post " +
            "JOIN User ON Post.userId = User.userId " +
            "WHERE user.username LIKE '%' || :query || '%' OR post.content LIKE '%' || :query || '%'" +
            "ORDER BY timestamp DESC"
    )
    fun getPostWithUserAndMediasByQuery(query: String): LiveData<List<PostWithUserAndMedia>>

    @Query(" SELECT post.postId, user.username, user.profilePictureUrl, post.content, COUNT(DISTINCT postlike.likeId) AS likeCount, COUNT(DISTINCT comment.commentId) AS commentCount, post.timestamp\n" +
            "FROM post\n" +
            "JOIN user ON post.userId = user.userId\n" +
            "LEFT JOIN comment ON post.postId = comment.postId\n" +
            "LEFT JOIN postlike ON post.postId = postlike.postId\n" +
            "WHERE post.userId = :userId\n" +
            "GROUP BY post.postId, user.username, user.profilePictureUrl, post.content, post.timestamp\n" +
            "ORDER BY post.timestamp DESC"
    )
    fun getPostWithUserByUserId(userId: String): LiveData<List<PostWithUser>>


    @Query(" SELECT post.postId, user.username, user.profilePictureUrl, post.content, COUNT(DISTINCT postlike.likeId) AS likeCount, COUNT(DISTINCT comment.commentId) AS commentCount, post.timestamp\n" +
            "FROM post\n" +
            "JOIN user ON post.userId = user.userId\n" +
            "LEFT JOIN comment ON post.postId = comment.postId\n" +
            "LEFT JOIN postlike ON post.postId = postlike.postId\n" +
            "WHERE user.username LIKE '%' || :query || '%' OR post.content LIKE '%' || :query || '%'" +
            "GROUP BY post.postId, user.username, user.profilePictureUrl, post.content, post.timestamp\n" +
            "ORDER BY post.timestamp DESC"
    )
    fun getPostWithUserByText(query: String): LiveData<List<PostWithUser>>

}