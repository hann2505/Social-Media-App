package com.example.socialmediaapp.data.room.like.post

import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.data.room.like.LikeDao

class PostLikeRepository(
    private val likeDao: LikeDao
) {
    suspend fun addPostLike(postLike: PostLike) {
        likeDao.addPostLike(postLike)
    }

    suspend fun deletePostLike(postLike: PostLike) {
        likeDao.deletePostLike(postLike)
    }

    fun checkIfLiked(userId: String, postId: String): LiveData<Boolean> {
        return likeDao.checkIfLiked(userId, postId)
    }

    fun getLikesByPostId(postId: String): LiveData<List<PostLike>> {
        return likeDao.getPostLikesByPostId(postId)
    }

    fun getPostIdByUserId(postId: String): LiveData<List<String>> {
        return likeDao.getPostIdByUserIds(postId)
    }

    fun getPostLikeCount(postId: String): LiveData<Int> {
        return likeDao.getPostLikeCount(postId)
    }
}