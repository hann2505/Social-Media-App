package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Follower(
    val fid: String = "",
    val followerId: String = "",
    val followingId: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
