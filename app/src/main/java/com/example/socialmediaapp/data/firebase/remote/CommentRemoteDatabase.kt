package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.comment.Comment
import com.example.socialmediaapp.data.entity.user.User
import com.example.socialmediaapp.other.Constant.COLLECTION_COMMENTS
import com.example.socialmediaapp.other.Constant.COLLECTION_POSTS
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class CommentRemoteDatabase @Inject constructor(
    db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val userCollection = db.collection(COLLECTION_USERS)


    fun addComment(user: User, postOwnerId: String, postId: String, content: String) {
        val commentCollection = userCollection
            .document(postOwnerId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_COMMENTS)

        val comment = Comment(
            commentCollection.document().id,
            postId,
            user.username,
            user.profilePictureUrl,
            content
        )
        commentCollection.document(comment.commentId).set(comment)
    }

    fun observeCommentCount(postOwnerId: String, postId: String, onUpdate: (Int) -> Unit) {
        val postLikesCollection = userCollection
            .document(postOwnerId)
            .collection(COLLECTION_POSTS)
            .document(postId)
            .collection(COLLECTION_COMMENTS)

        postLikesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val likeCount = snapshot?.count() ?: 0
            onUpdate(likeCount)
        }
    }

    fun getCommentsRealtimeUpdates(postOwnerId: String, postId: String, onUpdate: (List<Comment>) -> Unit) {
        userCollection.document(postOwnerId).collection(COLLECTION_POSTS).document(postId).collection(COLLECTION_COMMENTS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                val comments = snapshot?.documents?.mapNotNull {
                    it.toObject(Comment::class.java)
                }?: emptyList()
                onUpdate(comments)
            }

    }

}