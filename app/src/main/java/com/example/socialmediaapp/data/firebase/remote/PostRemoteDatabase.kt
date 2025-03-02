package com.example.socialmediaapp.data.firebase.remote

import android.net.Uri
import android.util.Log
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostMedia
import com.example.socialmediaapp.data.entity.PostWithUserAndMedia
import com.example.socialmediaapp.extensions.TimeConverter
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_MEDIAS
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
    private val postMediasCollection = db.collection(COLLECTION_POST_MEDIAS)
    private val storageRef = storage.reference

    suspend fun getAllPost(): List<Post> {
        return try {
            val documents = postsCollection.get().await()

            documents.map { document ->
                Post(
                    postId = document.getString("postId") ?: "",
                    userId = document.getString("userId") ?: "",
                    content = document.getString("content") ?: "",
                    mediaType = document.getString("mediaType")?.toMediaType() ?: MediaType.TEXT,
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

    private fun uploadPostMedia(postMedia: PostMedia) {
        try {
            postMediasCollection.document(postMedia.imageId).set(postMedia)
        } catch (e: Exception) {
            Log.e("PostRemoteDatabase", "Upload failed: ${e.message}", e)
        }
    }

    private fun uploadImageToStorage(fileName: String, imageUris: List<Uri>, onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0

        Log.d("Upload", "Image URIs: $imageUris")
        Log.d("Upload", "Image URIs: ${imageUris.size}")

        for (imageUri in imageUris) {
            val imageRef = storageRef.child(fileName + "${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { url ->
                    println("uri:  $imageUri\n" + "url: $url")
                    uploadedUrls.add(url.toString())
                    uploadCount++
                    println("count: $uploadCount")

                    if (uploadCount == imageUris.size) {
                        onSuccess(uploadedUrls)
                    }
                }.addOnFailureListener { exception ->
                    onFailure(exception)
                }
            }
        }
    }

    fun handleImageUpload(
        userId: String,
        content: String,
        imageUri: List<Uri>,
        postState: Boolean
    ) {
        val postId = postsCollection.document().id
        val fileName = "posts/${userId}/$postId/"
        val startTime = System.currentTimeMillis()
        uploadImageToStorage(
            fileName,
            imageUri,
            onSuccess = { downloadListUrl ->
                Log.d("Upload", "Success: $downloadListUrl")
                val post = Post(
                    postId = postId,
                    userId = userId,
                    content = content,
                    postState = postState
                )
                uploadPost(post)
                for (downloadUrl in downloadListUrl) {
                    val postMedia = PostMedia(
                        postMediasCollection.document().id,
                        postId,
                        downloadUrl
                    )
                    uploadPostMedia(postMedia)
                }
                val endTime = System.currentTimeMillis()
                val executionTime = endTime - startTime
                Log.d("Upload", "Execution time: ${TimeConverter.convertTimestampToDateTime(executionTime)} ms")
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
                    val mediaType = docChange.document.getString("mediaType")?.toMediaType() ?: MediaType.TEXT
                    val postState = docChange.document.getBoolean("postState") ?: true
                    val timestamp = docChange.document.getLong("timestamp") ?: continue
                    val post = Post(postId, userId, content, mediaType, postState, timestamp)

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

    fun listenForPostMediaChanges(onPostMediaChange: (FirebaseChangeType, PostMedia) -> Unit) {
        postMediasCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            snapshots?.let {
                for (docChange in it.documentChanges) {
                    val imageId = docChange.document.getString("imageId") ?: continue
                    val postId = docChange.document.getString("postId") ?: continue
                    val mediaUrl = docChange.document.getString("mediaUrl") ?: continue
                    val postMedia = PostMedia(imageId, postId, mediaUrl)

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
                    onPostMediaChange(result, postMedia)
                }

            }
        }
    }

}
