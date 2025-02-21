package com.example.socialmediaapp.data.firebase.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class UserAuthentication @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
            .addOnSuccessListener {
                it.user?.let { user ->
                    Log.d("log_in", "Success: ${user.email}")
                }
            }.addOnFailureListener {
                Log.d("log_in", "Failure: ${it.message}")
            }
    }

    fun register(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, task.result?.user!!.uid)
                }
            }.addOnFailureListener { exception ->
                onResult(false, exception.message.toString())
            }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun resetPassword(email: String, onResult: (Boolean) -> Unit) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                onResult(task.isSuccessful)
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}