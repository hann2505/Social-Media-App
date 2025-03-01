package com.example.socialmediaapp.data.room.media

import com.example.socialmediaapp.data.entity.PostMedia

class PostMediaRepository(
    private val postMediaDao: PostMediaDao
) {
    suspend fun upsertPostMedia(postMedia: PostMedia) {
        postMediaDao.upsertPostMedia(postMedia)
    }

    suspend fun deletePostMedia(postMedia: PostMedia) {
        postMediaDao.deletePostMedia(postMedia)
    }

    suspend fun getPostMediaByPostId(postId: String): List<PostMedia> {
        return postMediaDao.getPostMediaByPostId(postId)
    }
}