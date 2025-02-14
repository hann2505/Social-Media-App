package com.example.socialmediaapp.data.room.follower

import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.Follower

class FollowerRepository(
    private val followerDao: FollowerDao
) {
    val readAllDatabase: LiveData<List<Follower>> = followerDao.getAllFollowers()

    suspend fun upsertFollowers(followers: List<Follower>) {
        followerDao.upsertFollowers(followers)
    }

    suspend fun upsertFollower(follower: Follower) {
        followerDao.upsertFollower(follower)
    }

    suspend fun deleteFollower(follower: Follower) {
        return followerDao.deleteFollower(follower)
    }

    fun getFollowingByFollowerId(userId: String): LiveData<List<Follower>> {
        return followerDao.getFollowingByFollowerId(userId)
    }

    fun getFollowersByFollowingId(userId: String): LiveData<List<Follower>> {
        return followerDao.getFollowersByFollowingId(userId)
    }

    fun getFollower(followerId: String, followingId: String): LiveData<Follower> {
        return followerDao.getFollower(followerId, followingId)
    }

    fun checkIfFollowing(followerId: String, followingId: String): LiveData<Int> {
        return followerDao.checkIfFollowing(followerId, followingId)
    }

}