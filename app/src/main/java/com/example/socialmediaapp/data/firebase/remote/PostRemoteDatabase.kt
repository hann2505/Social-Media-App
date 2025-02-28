package com.example.socialmediaapp.data.firebase.remote

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRemoteDatabase @Inject constructor(
    db: FirebaseFirestore,
    storage: FirebaseStorage
) {

    private val postsCollection = db.collection(COLLECTION_POSTS)
    private val storageRef = storage.reference

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

    private fun uploadPost(post: Post) {
        try {
            postsCollection.document(post.postId).set(post)
        } catch (e: Exception) {
            Log.e("PostRemoteDatabase", "Upload failed: ${e.message}", e)
        }
    }

    private fun uploadImageToStorage(fileName: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    onSuccess(uri.toString()) // Pass download URL back
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun handleImageUpload(
        userId: String,
        content: String,
        imageUri: Uri,
        mediaUrl: String,
        postState: Boolean,
        timestamp: Long
    ) {
        val postId = postsCollection.document().id
        val fileName = "posts/${userId}/$postId/${System.currentTimeMillis()}.jpg"
        uploadImageToStorage(
            fileName,
            imageUri,
            onSuccess = { downloadUrl ->
                val post = Post(
                    postId = postId,
                    userId = userId,
                    content = content,
                    imageUrl = downloadUrl,
                    mediaUrl = mediaUrl,
                    postState = postState,
                    timestamp = timestamp
                )
                uploadPost(post)
            },
            onFailure = { exception ->
                Log.e("Upload", "Failed to upload image", exception)
            }
        )
    }


    fun listenForPostChanges(onPostChange: (FirebaseChangeType, Post) -> Unit) {
        postsCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            snapshots?.let {
                for (docChange in it.documentChanges) {
                    val postId = docChange.document.getString("postId") ?: continue
                    val userId = docChange.document.getString("userId") ?: continue
                    val content = docChange.document.getString("content") ?: continue
                    val imageUrl = docChange.document.getString("imageUrl") ?: continue
                    val mediaType = docChange.document.getString("mediaType")?.toMediaType() ?: MediaType.TEXT
                    val mediaUrl = docChange.document.getString("mediaUrl") ?: continue
                    val postState = docChange.document.getBoolean("postState") ?: true
                    val timestamp = docChange.document.getLong("timestamp") ?: continue
                    val post = Post(postId, userId, content, imageUrl, mediaType, mediaUrl, postState, timestamp)

                    val result: FirebaseChangeType = when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            ADDED
                        }
                        DocumentChange.Type.REMOVED -> {
                            REMOVED
                        }
                        DocumentChange.Type.MODIFIED -> {
                            MODIFIED
                        }
                        else -> {
                            NOT_DETECTED
                        }
                    }
                    onPostChange(result, post)
                }

            }
        }
    }

}
