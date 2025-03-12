package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.firebase.authentication.UserAuthentication
import com.example.socialmediaapp.other.Constant.COLLECTION_FOLLOWERS
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FollowerRemoteDatabase @Inject constructor(
    private val db: FirebaseFirestore,
    private val userAuthentication: UserAuthentication,
) {

    private val userCollection = db.collection(COLLECTION_USERS)

    private fun getFollower(followerId: String, followingId: String): Follower {

        val followersCollection = userCollection.document(followerId).collection(COLLECTION_FOLLOWERS)

        return Follower(
            fid = followersCollection.document().id,
            followerId = followerId,
            followingId = followingId
        )
    }

    suspend fun followUser(followerId: String, followingId: String) {
        val followersCollection = userCollection.document(followerId).collection(COLLECTION_FOLLOWERS)

        val follower = getFollower(followerId, followingId)
        followersCollection.document(follower.fid).set(follower).await()

    }

    fun unfollowUser(followerId: String, followingId: String) {
        val followersCollection = userCollection.document(followerId).collection(COLLECTION_FOLLOWERS)

        followersCollection.whereEqualTo("followerId", followerId).whereEqualTo("followingId", followingId).get().addOnSuccessListener {
            for (document in it) {
                followersCollection.document(document.id).delete()
            }
        }
    }

    fun getFollowerCountUpdate(userId: String, onFollowerChange: (Int) -> Unit) {
        userCollection.document(userId).collection(COLLECTION_FOLLOWERS).addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            val followerCount = snapshot?.count() ?: 0
            onFollowerChange(followerCount)
        }

    }

}
