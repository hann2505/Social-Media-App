package com.example.socialmediaapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val userId: String = "",
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = ""
)
