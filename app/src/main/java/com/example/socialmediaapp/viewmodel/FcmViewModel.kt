package com.example.socialmediaapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.firebase.api.FirebaseAuthTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FcmViewModel (
    context: Context
): ViewModel() {

    private val fAuthTokenProvider = FirebaseAuthTokenProvider(context)

    private val _accessToken = MutableStateFlow("")
    val accessToken: StateFlow<String> = _accessToken

    fun getAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            _accessToken.value = fAuthTokenProvider.getAccessToken()
        }
    }
}