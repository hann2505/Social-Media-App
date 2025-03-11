package com.example.socialmediaapp.data.firebase.remote

import android.util.Log
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_LIKES
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostLikeRemoteFirebase @Inject constructor(
    db: FirebaseFirestore
) {
    private val postLikeCollection = db.collection(COLLECTION_POST_LIKES)

    suspend fun addPostLike(userId: String, postId: String) {
        try {
            val postLike = PostLike(
                postLikeCollection.document().id,
                userId,
                postId
            )
            postLikeCollection.document(postLike.likeId).set(postLike).await()
        } catch (e: Exception) {
            throw e
        }
    }

    fun deletePostLike(userId: String, postId: String) {
            postLikeCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("postId", postId)
                .get().addOnSuccessListener {
                    for (document in it) {
                        postLikeCollection.document(document.id).delete()
                    }
                }
    }

    fun getLikeCountRealtime(postId: String, onResult: (Int) -> Unit) {
        postLikeCollection.whereEqualTo("postId", postId).addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "Error listening for like count changes", error)
                return@addSnapshotListener
            }
            val likeCount = snapshots?.documents?.size ?: 0
            onResult(likeCount)
        }
    }

    suspend fun isLiked(userId: String, postId: String): Boolean {
        return try {
            val documents = postLikeCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("postId", postId)
                .get().await()

            !documents.isEmpty
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getLikedPostByCurrentUser(userId: String): List<String> {
        return try {
            val documents= postLikeCollection.whereEqualTo("userId", userId).get().await()
            documents.mapNotNull {
                it.getString("postId")
            }
        } catch (e: Exception) {
            throw e
        }
    }

    fun listenToPostLikesChange(userId: String, onPostLikeChange: (List<String>) -> Unit) {
        postLikeCollection.whereEqualTo("userId", userId).addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "Error listening for post like changes", error)
                return@addSnapshotListener
            }

            val likedPostIds = snapshots?.documents?.mapNotNull { it.getString("postId") } ?: emptyList()
            onPostLikeChange(likedPostIds)

        }
    }

}