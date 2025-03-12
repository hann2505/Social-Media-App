package com.example.socialmediaapp.data.firebase.remote

import android.net.Uri
import android.util.Log
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostMedia
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.extensions.TimeConverter
import com.example.socialmediaapp.other.Constant.COLLECTION_COMMENTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_LIKES
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_MEDIAS
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.*
import com.example.socialmediaapp.other.MediaTypeConverter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PostRemoteDatabase @Inject constructor(
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    private val mediaTypeConverter: MediaTypeConverter
) {
    private val usersCollection = db.collection(COLLECTION_USERS)
    private val postsCollection = usersCollection.document().collection(COLLECTION_POSTS)
    private val postGroupCollection = db.collectionGroup(COLLECTION_POST_LIKES)

    private val storageRef = storage.reference

    suspend fun getPostByUserIdFromFirebase(userId: String): List<PostWithUser> {
        return try {

            val user = usersCollection.document(userId).get().await().toObject<User>()

            val posts = usersCollection
                .document(userId)
                .collection(COLLECTION_POSTS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)

            posts.map {

                val postLikeCount = usersCollection.document(userId)
                    .collection(COLLECTION_POSTS)
                    .document(it.postId)
                    .collection(COLLECTION_POST_LIKES)
                    .get()
                    .await()
                    .size()


                val commentCount = usersCollection.document(userId)
                    .collection(COLLECTION_POSTS)
                    .document(it.postId)
                    .collection(COLLECTION_COMMENTS)
                    .get()
                    .await()
                    .size()

                PostWithUser(
                    postId = it.postId,
                    userId = user!!.userId,
                    username = user.username,
                    profilePictureUrl = user.profilePictureUrl,
                    content = it.content,
                    mediaUrls = it.listMediaUrls,
                    likeCount = postLikeCount,
                    commentCount = commentCount,
                    timestamp = it.timestamp
                )
            }
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    fun getPostCount(userId: String, onUpdate: (Int) -> Unit) {
        usersCollection.document(userId).collection(COLLECTION_POSTS).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("PostRemoteDatabase", "Listen failed.", e)
                return@addSnapshotListener
            }

            val postCount = snapshot?.count() ?: 0
            onUpdate(postCount)

        }
    }

    suspend fun getPostRealtimeUpdate(userId: String, onUpdate: (List<PostWithUser>) -> Unit): ListenerRegistration {
         return usersCollection
             .document(userId)
             .collection(COLLECTION_POSTS)
             .orderBy("timestamp", Query.Direction.DESCENDING)
             .addSnapshotListener { snapshot, e ->
                 if (e != null) {
                    Log.w("PostRemoteDatabase", "Listen failed.", e)
                    return@addSnapshotListener
                 }

                 if (snapshot == null) {
                    onUpdate(emptyList())
                    return@addSnapshotListener
                 }

                 CoroutineScope(Dispatchers.IO).launch {
                    val user = usersCollection.document(userId).get().await().toObject<User>()

                    val posts = snapshot.toObjects(Post::class.java)

                     val postWithUserList = posts.map { post ->
                         val postLikeCount = usersCollection.document(userId)
                             .collection(COLLECTION_POSTS)
                             .document(post.postId)
                             .collection(COLLECTION_POST_LIKES)
                             .get()
                             .await()
                             .size()

                         val commentCount = usersCollection.document(userId)
                             .collection(COLLECTION_POSTS)
                             .document(post.postId)
                             .collection(COLLECTION_COMMENTS)
                             .get()
                             .await()
                             .size()

                         PostWithUser(
                             postId = post.postId,
                             userId = user!!.userId,
                             username = user.username,
                             profilePictureUrl = user.profilePictureUrl,
                             content = post.content,
                             mediaUrls = post.listMediaUrls,
                             likeCount = postLikeCount,
                             commentCount = commentCount,
                             timestamp = post.timestamp
                         )
                     }

                     withContext(Dispatchers.Main) {
                         onUpdate(postWithUserList) // Send updated list to UI
                     }
                 }
            }



    }

    suspend fun searchPostsByContentOrUsername(query: String): List<PostWithUser> {
        return try {
            postGroupCollection
                .whereLessThanOrEqualTo("content", query)
                .whereGreaterThanOrEqualTo("content", query)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun String.toMediaType(): MediaType {
        return MediaType.entries.find {
            it.name.equals(this, ignoreCase = true)
        } ?: MediaType.TEXT
    }

    private fun uploadPost(userId: String, post: Post) {
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
            usersCollection.document(userId).collection(COLLECTION_POSTS).document(post.postId).set(postMap)
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
                uploadPost(userId, post)
            },
            onFailure = { exception ->
                Log.e("Upload", "Failed to upload image", exception)
            }
        )
    }

}
