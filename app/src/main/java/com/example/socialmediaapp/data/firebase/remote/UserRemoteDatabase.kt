package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDatabase @Inject constructor(
    private val db: FirebaseFirestore
) {
    private val usersCollection = db.collection(COLLECTION_USERS)

    suspend fun getAllUsers(): List<User> {
        return try {
            usersCollection.get().await().toObjects(User::class.java)
        }
        catch (e: Exception) {
            emptyList()
        }
    }
}