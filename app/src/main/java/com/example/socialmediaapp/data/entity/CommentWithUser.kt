package com.example.socialmediaapp.data.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommentWithUser(
    val commentId: String,
    val postId: String,
    val username: String,
    val profilePictureUrl: String,
    val content: String,
    val timestamp: Long
) : Parcelable
