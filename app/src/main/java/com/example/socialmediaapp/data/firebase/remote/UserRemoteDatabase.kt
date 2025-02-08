package com.example.socialmediaapp.data.firebase.remote

import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDatabase @Inject constructor(
    db: FirebaseFirestore
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

    suspend fun upsertUser(user: User) {
        usersCollection.document(user.userId).set(user).await()
    }

    suspend fun updateName(userId: String, name: String) {
        usersCollection.document(userId).update("name", name).await()
    }

    suspend fun updateUsername(userId: String, username: String) {
        usersCollection.document(userId).update("username", username).await()
    }

    suspend fun updateBio(userId: String, bio: String) {
        usersCollection.document(userId).update("bio", bio).await()
    }

    suspend fun updateGender(uid: String, gender: Boolean) {
        usersCollection.document(uid).update("gender", gender).await()
    }
}