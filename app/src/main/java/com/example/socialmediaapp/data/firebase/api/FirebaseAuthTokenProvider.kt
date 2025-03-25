package com.example.socialmediaapp.data.firebase.api

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials

class FirebaseAuthTokenProvider (
    private val context: Context
) {

    fun getAccessToken(): String {
        context.assets.list("")?.forEach {
            Log.d("Assets", "File: $it")
        }

        val inputStream = context.assets.open("service-account.json")
        Log.d("FirebaseAuthTokenProvider", "inputStream: ${inputStream}")
        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
        credentials.refreshIfExpired()
        Log.d("FirebaseAuthTokenProvider", "getAccessToken: ${credentials.accessToken.tokenValue}")
        return credentials.accessToken.tokenValue
    }
}