package com.example.socialmediaapp.data.room.follower

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.Follower

@Dao
interface FollowerDao {

    @Upsert
    suspend fun upsertFollowers(followers: List<Follower>)

    @Upsert
    suspend fun upsertFollower(follower: Follower)

    @Delete
    suspend fun deleteFollower(follower: Follower)

    @Query("SELECT * FROM Follower WHERE followerId = :followerId AND followingId = :followingId")
    fun getFollower(followerId: String, followingId: String): LiveData<Follower>

    @Query("SELECT * FROM Follower")
    fun getAllFollowers(): LiveData<List<Follower>>

    @Query("SELECT * FROM Follower WHERE followerId = :followerId")
    fun getFollowingByFollowerId(followerId: String): LiveData<List<Follower>>

    @Query("SELECT * FROM Follower WHERE followingId = :followingId")
    fun getFollowersByFollowingId(followingId: String): LiveData<List<Follower>>

    @Query("SELECT COUNT(fid) FROM Follower WHERE followerId = :followerId AND followingId = :followingId")
    fun checkIfFollowing(followerId: String, followingId: String): LiveData<Int>

    @Query("DELETE FROM Follower WHERE followerId = :followerId AND followingId = :followingId")
    fun deleteFollower(followerId: String, followingId: String)

}