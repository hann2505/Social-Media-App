package com.example.socialmediaapp.data.firebase.remote

import android.util.Log
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.*
import com.google.firebase.firestore.DocumentChange
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

    fun listenForUsersChanges(onUserChange: (FirebaseChangeType, User) -> Unit) {
        usersCollection.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.e("Firestore", "Error listening for user changes", error)
                return@addSnapshotListener
            }

            snapshots?.let {
                for (docChange in it.documentChanges) {
                    val userId = docChange.document.getString("userId") ?: continue
                    val name = docChange.document.getString("name") ?: continue
                    val username = docChange.document.getString("username") ?: continue
                    val bio = docChange.document.getString("bio") ?: continue
                    val gender = docChange.document.getBoolean("gender") ?: continue
                    val email = docChange.document.getString("email") ?: continue
                    val profilePicture = docChange.document.getString("profilePicture") ?: continue

                    val user = User(userId, name, username, gender, bio, email, profilePicture)

                    val result: FirebaseChangeType = when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            ADDED
                        }
                        DocumentChange.Type.REMOVED -> {
                            REMOVED
                        }
                        else -> {
                            NOT_DETECTED
                        }
                    }

                    onUserChange(result, user)
                }
            }
        }
    }
}