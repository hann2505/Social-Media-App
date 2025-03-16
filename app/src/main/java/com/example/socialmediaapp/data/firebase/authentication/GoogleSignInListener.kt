package com.example.socialmediaapp.data.firebase.authentication

import com.google.firebase.auth.FirebaseUser

interface GoogleSignInListener {
    fun onSignInSuccess(user: FirebaseUser?)
    fun onSignInFailure(errorMessage: String)
}