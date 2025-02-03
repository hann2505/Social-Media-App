package com.example.socialmediaapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "User")
data class User(
    @PrimaryKey
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val gender: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = "",
    @ColumnInfo(name = "followers", defaultValue = "0")
    val followers: Int = 0,
    @ColumnInfo(name = "following", defaultValue = "0")
    val following: Int = 0,
    @ColumnInfo(name = "posts", defaultValue = "0")
    val posts: Int = 0
): Parcelable
