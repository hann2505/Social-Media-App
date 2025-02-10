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

    fun getFollowingByFollowerId(userId: String): LiveData<List<Follower>> {
        return followerDao.getFollowingByFollowerId(userId)
    }

    fun getFollowersByFollowingId(userId: String): LiveData<List<Follower>> {
        return followerDao.getFollowersByFollowingId(userId)
    }

}