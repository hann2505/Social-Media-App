package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Follower")
data class Follower(
    @PrimaryKey(autoGenerate = false)
    val fid: String = "",
    val followerId: String = "",
    val followingId: String = "",
    val timestamp: Long = 0
)
