package com.example.socialmediaapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val userId: String = "",
    @ColumnInfo(name = "name", defaultValue = "")
    val name: String = "",
    @ColumnInfo(name = "username", defaultValue = "")
    val username: String = "",
    @ColumnInfo(name = "gender", defaultValue = "0")
    val gender: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = ""
)
