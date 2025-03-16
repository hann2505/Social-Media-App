package com.example.socialmediaapp.data.entity.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val gender: Boolean = false,
    val email: String = "",
    val bio: String = "",
    val profilePictureUrl: String = "",
): Parcelable
