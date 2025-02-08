package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRemoteDatabase @Inject constructor(
    private val db: FirebaseFirestore
) {

    private val postsCollection = db.collection(COLLECTION_POSTS)

    //TODO check the result documents.map
    suspend fun getAllPost(): List<Post> {
        return try {
            val documents = postsCollection.get().await()

            documents.map { document ->
                Post(
                    postId = document.getString("postId") ?: "",
                    userId = document.getString("userId") ?: "",
                    content = document.getString("content") ?: "",
                    imageUrl = document.getString("imageUrl") ?: "",
                    mediaType = document.getString("mediaType")?.toMediaType() ?: MediaType.TEXT, // Convert String to Enum
                    mediaUrl = document.getString("mediaUrl") ?: "",
                    postState = document.getBoolean("postState") ?: true,
                    timestamp = document.getLong("timestamp") ?: 0
                )
            }
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    private fun String.toMediaType(): MediaType {
        return MediaType.entries.find {
            it.name.equals(this, ignoreCase = true)
        } ?: MediaType.TEXT
    }

    suspend fun uploadPost(
        userId: String,
        content: String,
        imageUrl: String,
        mediaUrl: String,
        postState: Boolean,
        timestamp: Long
    ) {
        val mediaType = when {
            imageUrl.isNotEmpty() -> "image"
            mediaUrl.isNotEmpty() -> "video"
            else -> "text"
        }
        val post = Post(
            postId = postsCollection.document().id,
            userId = userId,
            content = content,
            imageUrl = imageUrl,
            mediaUrl = mediaUrl,
            postState = postState,
            timestamp = timestamp
        )
        postsCollection.document(post.postId).set(post).await()

    }

}