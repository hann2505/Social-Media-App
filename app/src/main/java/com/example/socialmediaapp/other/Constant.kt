package com.example.socialmediaapp.other

import com.example.socialmediaapp.data.entity.MediaType

object Constant {
    val COLLECTION_USERS = "User"
    val COLLECTION_POSTS = "Post"
    val COLLECTION_COMMENTS = "Comment"
    val COLLECTION_POST_LIKES = "Post Like"
    val COLLECTION_COMMENT_LIKES = "Comment Like"
    val COLLECTION_FOLLOWERS = "Follower"
    val COLLECTION_FOLLOWING = "Following"

    val mediaTypeMap = hashMapOf(
        "image" to MediaType.IMAGE,
        "video" to MediaType.VIDEO,
        "text" to MediaType.TEXT
    )
}