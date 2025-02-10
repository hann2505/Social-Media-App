package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.other.Constant.COLLECTION_FOLLOWERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FollowerRemoteDatabase @Inject constructor(
    private val db: FirebaseFirestore
) {

    private val followersCollection = db.collection(COLLECTION_FOLLOWERS)

    suspend fun followUser(followerId: String, followingId: String) {
        val follower = Follower(
            fid = followersCollection.document().id,
            followerId = followerId,
            followingId = followingId,
            timestamp = System.currentTimeMillis()
        )
        followersCollection.document(follower.fid).set(follower).await()

    }

    suspend fun getAllFollower(): List<Follower> {
        return try {
            followersCollection.get().await().toObjects(Follower::class.java)
        }
        catch (e: Exception) {
            emptyList()
        }
    }
}