package com.example.socialmediaapp.data.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    @PrimaryKey
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val gender: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = "",
): Parcelable
