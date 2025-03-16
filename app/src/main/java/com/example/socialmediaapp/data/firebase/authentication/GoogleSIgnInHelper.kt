package com.example.socialmediaapp.data.firebase.authentication

import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.socialmediaapp.R
import com.example.socialmediaapp.data.entity.user.User
import com.example.socialmediaapp.data.firebase.remote.UserRemoteDatabase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class GoogleSignInHelper @Inject constructor (
    private val firebaseAuth: FirebaseAuth,
    private val userRemoteDatabase: UserRemoteDatabase
) {

    private var listener: GoogleSignInListener? = null
    private lateinit var googleSignInClient: GoogleSignInClient

    fun init(fragment: Fragment, listener: GoogleSignInListener) {
        this.listener = listener

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
    }

    fun signIn(fragment: Fragment) {
        val signInIntent = googleSignInClient.signInIntent
        fragment.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        googleSignInClient.signOut().addOnCompleteListener {
            firebaseAuth.signOut()
            listener?.onSignInFailure("User signed out")
        }
    }

    fun handleSignInResult(requestCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "Google sign in failed", e)
                listener?.onSignInFailure("Google sign-in failed: ${e.message}")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    listener?.onSignInSuccess(firebaseAuth.currentUser)
                    val user = User(
                        userId = firebaseAuth.currentUser!!.uid,
                        username = firebaseAuth.currentUser!!.displayName.toString(),
                        email = firebaseAuth.currentUser!!.email.toString(),
                        profilePictureUrl = firebaseAuth.currentUser?.photoUrl.toString()
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        userRemoteDatabase.upsertUser(user)
                    }

                } else {
                    listener?.onSignInFailure("Authentication failed")
                }
            }
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
