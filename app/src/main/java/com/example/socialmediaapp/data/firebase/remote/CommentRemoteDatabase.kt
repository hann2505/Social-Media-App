package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.other.Constant.COLLECTION_COMMENTS
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.NOT_DETECTED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import com.google.firebase.firestore.DocumentChange.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRemoteDatabase @Inject constructor(
    db: FirebaseFirestore
) {

    private val commentCollection = db.collection(COLLECTION_COMMENTS)

    suspend fun getAllComments(): List<Comment> {
        return try {
            commentCollection.get().await().toObjects(Comment::class.java)
        }
        catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addComment(comment: Comment) {
        try {
            commentCollection.add(comment).await()
        } catch (e: Exception) {
            throw e
        }
    }

    fun listenForCommentsChanges(onCommentChange: (FirebaseChangeType, Comment) -> Unit) {
        commentCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            snapshots?.let {
                for (docChange in it.documentChanges) {
                    val comment = docChange.document.toObject(Comment::class.java)
                    val result: FirebaseChangeType = when (docChange.type) {
                        Type.ADDED -> {
                            ADDED
                        }
                        Type.REMOVED -> {
                            REMOVED
                        }
                        else -> {
                            NOT_DETECTED
                        }
                    }
                    onCommentChange(result, comment)
                }
            }
        }
    }

}