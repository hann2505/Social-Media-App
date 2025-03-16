package com.example.socialmediaapp.data.firebase.remote

import android.util.Log
import com.example.socialmediaapp.data.entity.like.PostLike
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POST_LIKES
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class PostLikeRemoteFirebase @Inject constructor(
    db: FirebaseFirestore
) {
    private val userCollection = db.collection(COLLECTION_USERS)
    private val postCollection = db.collectionGroup(COLLECTION_POSTS)

    fun addPostLike(currentUserId: String, userId: String, postId: String) {
        val postLikesCollection = userCollection
            .document(userId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_POST_LIKES)

        val postLike = PostLike(
            postLikesCollection.document().id,
            currentUserId,
            postId
        )
        postLikesCollection.document(postLike.likeId).set(postLike).addOnSuccessListener {
            Log.d("post", "addPostLike: success")
        }.addOnFailureListener {
            Log.d("post", "addPostLike: failed")
        }
    }

    fun deletePostLike(currentUserId: String, userId: String, postId: String) {
        val postLikesCollection = userCollection
            .document(userId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_POST_LIKES)

        postLikesCollection
                .whereEqualTo("userId", currentUserId)
                .whereEqualTo("postId", postId)
                .get().addOnSuccessListener {
                    for (document in it) {
                        postLikesCollection.document(document.id).delete()
                    }
                }
    }

    fun observeLikeCount(postOwnerId: String, postId: String, onUpdate: (Int) -> Unit) {
        val postLikesCollection = userCollection
            .document(postOwnerId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_POST_LIKES)

        postLikesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val likeCount = snapshot?.count() ?: 0
            onUpdate(likeCount)
        }
    }

    fun isLiked(currentUserId: String, userId: String, postId: String, onResult: (Boolean) -> Unit) {
        val postLikesCollection = userCollection
            .document(userId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_POST_LIKES)

        postLikesCollection
            .whereEqualTo("userId", currentUserId)
            .whereEqualTo("postId", postId)
            .get().addOnSuccessListener {
                onResult(!it.isEmpty)
            }.addOnFailureListener {
                onResult(false)
            }

    }


}