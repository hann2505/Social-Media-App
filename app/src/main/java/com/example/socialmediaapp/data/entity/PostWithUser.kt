package com.example.socialmediaapp.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostWithUser(
    val postId: String,
    val username: String,
    val profilePictureUrl: String,
    val content: String,
    val likeCount: Int,
    val commentCount : Int,
    val timestamp: Long,
) : Parcelable
