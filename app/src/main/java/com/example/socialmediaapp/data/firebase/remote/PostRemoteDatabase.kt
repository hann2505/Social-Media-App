package com.example.socialmediaapp.data.firebase.remote

import android.net.Uri
import android.util.Log
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostMedia
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.extensions.TimeConverter
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_LIKES
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_MEDIAS
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.*
import com.example.socialmediaapp.other.MediaTypeConverter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRemoteDatabase @Inject constructor(
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    private val mediaTypeConverter: MediaTypeConverter
) {
    private val usersCollection = db.collection(COLLECTION_USERS)
    private val postsCollection = usersCollection.document().collection(COLLECTION_POSTS)
    private val likesCollection = db.collection(COLLECTION_POST_LIKES)
    private val postMediasCollection = db.collection(COLLECTION_POST_MEDIAS)
    private val storageRef = storage.reference

    suspend fun getPostByUserIdFromFirebase(userId: String): List<PostWithUser> {
        return try {
            val userSnapshot = usersCollection.document(userId).get().await()
            val user = userSnapshot.toObject(User::class.java) ?: return emptyList()

            val documents = postsCollection.whereEqualTo("userId", userId).get().await()

            val postIds = documents.documents.mapNotNull { it.getString("postId") }
            val likesSnapshot = likesCollection.whereIn("postId", postIds).get().await()

            val likeCounts = likesSnapshot.documents.groupBy { it.getString("postId") }
                .mapValues { it.value.size }

            documents.map { document ->
                val postId = document.getString("postId") ?: ""

                PostWithUser(
                    postId = postId,
                    username = user.username, // From user document
                    profilePictureUrl = user.profilePictureUrl, // From user document
                    content = document.getString("content") ?: "",
                    mediaUrls = document.get("listMediaUrls") as? List<String> ?: emptyList(),
                    likeCount = likeCounts[postId] ?: 0,
                    commentCount = (document.getLong("commentCount") ?: 0).toInt(),
                    timestamp = document.getLong("timestamp") ?: 0
                )
            }
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    fun getPostWithUserRealTime(userId: String, onResult: (List<PostWithUser>) -> Unit) {
        usersCollection.document(userId).addSnapshotListener { userSnapshot, error ->
            if (error != null || userSnapshot == null || !userSnapshot.exists()) {
                onResult(emptyList())
                return@addSnapshotListener
            }

            val user = userSnapshot.toObject(User::class.java) ?: return@addSnapshotListener

            postsCollection.whereEqualTo("userId", userId)
                .addSnapshotListener { postSnapshot, postError ->
                    if (postError != null || postSnapshot == null) {
                        onResult(emptyList())
                        return@addSnapshotListener
                    }

                    val postIds = postSnapshot.documents.mapNotNull { it.getString("postId") }

                    likesCollection.whereIn("postId", postIds).addSnapshotListener { likesSnapshot, likeError ->
                        if (likeError != null || likesSnapshot == null) {
                            onResult(emptyList())
                            return@addSnapshotListener
                        }

                        val likeCounts = likesSnapshot.documents.groupBy { it.getString("postId") }
                            .mapValues { it.value.size }

                        val postsWithUser = postSnapshot.documents.map { document ->
                            val postId = document.getString("postId") ?: ""

                            PostWithUser(
                                postId = postId,
                                username = user.username,
                                profilePictureUrl = user.profilePictureUrl,
                                content = document.getString("content") ?: "",
                                mediaUrls = document.get("listMediaUrls") as? List<String> ?: emptyList(),
                                likeCount = likeCounts[postId] ?: 0,
                                commentCount = (document.getLong("commentCount") ?: 0).toInt(),
                                timestamp = document.getLong("timestamp") ?: 0
                            )
                        }

                        onResult(postsWithUser)
                    }
                }
        }
    }

    private fun String.toMediaType(): MediaType {
        return MediaType.entries.find {
            it.name.equals(this, ignoreCase = true)
        } ?: MediaType.TEXT
    }

    private fun uploadPost(post: Post) {
        try {
            val postMap = mapOf(
                "postId" to post.postId,
                "userId" to post.userId,
                "content" to post.content,
                "listMediaUrls" to post.listMediaUrls,
                "mediaType" to post.mediaType.name, // Convert enum to String
                "postState" to post.postState,
                "timestamp" to post.timestamp
            )
            postsCollection.document(post.postId).set(postMap)
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
                    Log.d("Fail to upload", "Failed to upload image: $exception")
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
        uploadImageToStorage(
            fileName,
            imageUri,
            onSuccess = { downloadListUrl ->
                Log.d("Upload", "Success: $downloadListUrl")
                val post = Post(
                    postId = postId,
                    userId = userId,
                    content = content,
                    listMediaUrls = downloadListUrl,
                    mediaType = MediaType.IMAGE,
                    postState = postState
                )
                uploadPost(post)
            },
            onFailure = { exception ->
                Log.e("Upload", "Failed to upload image", exception)
            }
        )
    }

    fun postChangesOnce(onPostChange: (FirebaseChangeType, Post) -> Unit) {
        postsCollection.get().addOnSuccessListener { snapshots ->
            for (docChange in snapshots.documentChanges) {
                val post = docChange.document.toObject(Post::class.java)
                val result = when (docChange.type) {
                    DocumentChange.Type.ADDED -> ADDED
                    DocumentChange.Type.REMOVED -> REMOVED
                    DocumentChange.Type.MODIFIED -> MODIFIED
                    else -> NOT_DETECTED
                }
                onPostChange(result, post)
            }
        }
            .addOnFailureListener { exception ->
                Log.e("PostRemoteDatabase", "Failed to fetch posts", exception)
            }

    }

    fun postMediaChangesOnce(onPostChange: (FirebaseChangeType, PostMedia) -> Unit) {
        postMediasCollection.get().addOnSuccessListener { snapshots ->
            for (docChange in snapshots.documentChanges) {
                val post = docChange.document.toObject(PostMedia::class.java)
                val result = when (docChange.type) {
                    DocumentChange.Type.ADDED -> ADDED
                    DocumentChange.Type.REMOVED -> REMOVED
                    DocumentChange.Type.MODIFIED -> MODIFIED
                    else -> NOT_DETECTED
                }
                onPostChange(result, post)
            }
        }
            .addOnFailureListener { exception ->
                Log.e("PostRemoteDatabase", "Failed to fetch posts", exception)
            }

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
                    val listMediaUrls = docChange.document.get("listMediaUrls") as? List<String> ?: continue
                    val mediaType = docChange.document.getString("mediaType")?.toMediaType() ?: MediaType.TEXT
                    val postState = docChange.document.getBoolean("postState") ?: true
                    val timestamp = docChange.document.getLong("timestamp") ?: continue
                    val post = Post(postId, userId, content, listMediaUrls, mediaType, postState, timestamp)

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
