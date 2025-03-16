package com.example.socialmediaapp.data.entity.follower

data class Follower(
    val fid: String = "",
    val followerId: String = "",
    val followingId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
