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

    fun listenToPostLikesChange(onPostLikeChange: (FirebaseChangeType, PostLike) -> Unit) {
        postLikeCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "Error listening for post like changes", error)
                return@addSnapshotListener
            }

            snapshots?.let {
                for (docChange in it.documentChanges) {
                    val likeId = docChange.document.getString("likeId") ?: continue
                    val userId = docChange.document.getString("userId") ?: continue
                    val postId = docChange.document.getString("postId") ?: continue
                    val timestamp = docChange.document.getLong("timestamp") ?: continue

                    val postLike = PostLike(likeId, userId, postId, timestamp)

                    val result: FirebaseChangeType = when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            ADDED
                        }
                        DocumentChange.Type.REMOVED -> {
                            REMOVED
                        }
                        else -> FirebaseChangeType.NOT_DETECTED
                    }

                    onPostLikeChange(result, postLike)
                }
            }
        }
    }

}