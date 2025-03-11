package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.firebase.remote.FollowerRemoteDatabase
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    application: Application,
    private val followerRemoteDatabase: FollowerRemoteDatabase
) : AndroidViewModel(application)
{

    fun followUser(followerId: String, followingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.followUser(followerId, followingId)
        }

    }

    fun unfollowUser(followerId: String, followingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.unfollowUser(followerId, followingId)
        }

    }

    fun checkIfFollowingChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.listenForFollowerChanges {changeType, follower ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("follow", changeType.toString())
                    when (changeType) {
                        ADDED -> {
                            Log.d("follow", "added: $follower")
                        }
                        REMOVED -> {
                            Log.d("follow", "deleted: $follower")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}