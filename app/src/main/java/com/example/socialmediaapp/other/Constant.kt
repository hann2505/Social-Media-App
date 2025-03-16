package com.example.socialmediaapp.other

import com.example.socialmediaapp.data.entity.post.MediaType

object Constant {
    val COLLECTION_USERS = "User"
    val COLLECTION_POSTS = "Post"
    val COLLECTION_COMMENTS = "Comment"
    val COLLECTION_POST_LIKES = "Post Like"
    val COLLECTION_FOLLOWERS = "Follower"
    val COLLECTION_MESSAGES = "Message"
    val SCROLL_POSITION = "Scroll position"

    val mediaTypeMap = hashMapOf(
        "image" to MediaType.IMAGE,
        "video" to MediaType.VIDEO,
        "text" to MediaType.TEXT
    )
}