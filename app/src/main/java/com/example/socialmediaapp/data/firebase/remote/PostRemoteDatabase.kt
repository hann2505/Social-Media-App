package com.example.socialmediaapp.data.firebase.remote

import android.net.Uri
import android.util.Log
import com.example.socialmediaapp.data.entity.MediaType
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.other.Constant.COLLECTION_COMMENTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_LIKES
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.example.socialmediaapp.other.MediaTypeConverter
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
import javax.inject.Inject

class PostRemoteDatabase @Inject constructor(
    db: FirebaseFirestore,
    storage: FirebaseStorage,
    private val mediaTypeConverter: MediaTypeConverter
) {
    private val usersCollection = db.collection(COLLECTION_USERS)
    private val postsCollection = usersCollection.document().collection(COLLECTION_POSTS)
    private val postGroupCollection = db.collectionGroup(COLLECTION_POSTS)

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

    suspend fun getNewestPost(): List<PostWithUser> {
        return try {
            val posts = postGroupCollection
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()
                .toObjects<Post>()

            Log.d("New feed", "Post List: $posts")

            // Extract unique user IDs from the posts
            val userIds = posts.map { it.userId }.toSet()

            // Fetch user details in parallel for performance
            val usersMap = userIds.associateWith { userId ->
                usersCollection.document(userId).get().await().toObject<User>()
            }

            // Map posts to PostWithUser by attaching the corresponding user data
            posts.mapNotNull { post ->
                val user = usersMap[post.userId]
                user?.let {
                    val postLikeCount = usersCollection.document(it.userId)
                        .collection(COLLECTION_POSTS)
                        .document(post.postId)
                        .collection(COLLECTION_POST_LIKES)
                        .get()
                        .await()
                        .size()

                    val commentCount = usersCollection.document(it.userId)
                        .collection(COLLECTION_POSTS)
                        .document(post.postId)
                        .collection(COLLECTION_COMMENTS)
                        .get()
                        .await()
                        .size()

                    PostWithUser(
                        postId = post.postId,
                        userId = it.userId,
                        username = it.username,
                        profilePictureUrl = it.profilePictureUrl,
                        content = post.content,
                        mediaUrls = post.listMediaUrls,
                        likeCount = postLikeCount,
                        commentCount = commentCount,
                        timestamp = post.timestamp
                    )
                }  // Only include posts where user data exists
            }
        } catch (e: Exception) {
            Log.e("New feed", "Search failed: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun getNotification(userList: List<String>, onUpdate: (List<PostWithUser>) -> Unit) {
        val notificationList = mutableListOf<PostWithUser>()
        for (user in userList) {
            notificationList.addAll(getPostByUserIdFromFirebase(user))
        }
        notificationList.sortByDescending {
            it.timestamp
        }
        onUpdate(notificationList)
    }

    suspend fun searchPostsByContentOrUsername(query: String): List<PostWithUser> {
        return try {
            // Fetch posts that match the query
            val posts = postGroupCollection
                .whereGreaterThanOrEqualTo("content", query)
                .whereLessThan("content", query + "\uf8ff")
                .orderBy("content")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects<Post>()

            Log.d("Search Post", "Post List: $posts")

            // Extract unique user IDs from the posts
            val userIds = posts.map { it.userId }.toSet()

            // Fetch user details in parallel for performance
            val usersMap = userIds.associateWith { userId ->
                usersCollection.document(userId).get().await().toObject<User>()
            }

            // Map posts to PostWithUser by attaching the corresponding user data
            posts.mapNotNull { post ->
                val user = usersMap[post.userId]
                user?.let {
                    val postLikeCount = usersCollection.document(it.userId)
                        .collection(COLLECTION_POSTS)
                        .document(post.postId)
                        .collection(COLLECTION_POST_LIKES)
                        .get()
                        .await()
                        .size()

                    val commentCount = usersCollection.document(it.userId)
                        .collection(COLLECTION_POSTS)
                        .document(post.postId)
                        .collection(COLLECTION_COMMENTS)
                        .get()
                        .await()
                        .size()

                    PostWithUser(
                        postId = post.postId,
                        userId = it.userId,
                        username = it.username,
                        profilePictureUrl = it.profilePictureUrl,
                        content = post.content,
                        mediaUrls = post.listMediaUrls,
                        likeCount = postLikeCount,
                        commentCount = commentCount,
                        timestamp = post.timestamp
                    )
                }  // Only include posts where user data exists
            }
        } catch (e: Exception) {
            Log.e("SearchPost", "Search failed: ${e.message}", e)
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
