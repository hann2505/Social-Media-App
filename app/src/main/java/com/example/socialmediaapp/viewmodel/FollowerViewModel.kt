package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.follower.Follower
import com.example.socialmediaapp.data.firebase.remote.FollowerRemoteDatabase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowerViewModel @Inject constructor(
    application: Application,
    private val followerRemoteDatabase: FollowerRemoteDatabase,
    private val firebaseMessaging: FirebaseMessaging
) : AndroidViewModel(application)
{
    private val _followers = MutableLiveData<List<Follower>>()
    val followers: LiveData<List<Follower>> = _followers

    private val _followerCount = MutableLiveData<Int>()
    val followerCount: LiveData<Int> = _followerCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> = _followingCount

    private val _followState = MutableStateFlow(false)
    val followState: MutableStateFlow<Boolean> = _followState

    private val _followingIds = MutableStateFlow(emptyList<String>())
    val followingIds: MutableStateFlow<List<String>> = _followingIds

    fun followUser(followerId: String, followingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.followUser(followerId, followingId)
            firebaseMessaging.subscribeToTopic("user_$followingId").addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FollowerViewModel", "Subscribed to topic: user_$followingId")
                } else {
                    Log.d("FollowerViewModel", "Failed to subscribe to topic: user_$followingId")
                }
            }
        }

    }

    fun unfollowUser(followerId: String, followingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.unfollowUser(followerId, followingId)
            firebaseMessaging.unsubscribeFromTopic("user_$followingId")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("FollowerViewModel", "unsubscribed to topic: user_$followingId")
                    } else {
                        Log.d(
                            "FollowerViewModel",
                            "Failed to unsubscribe to topic: user_$followingId"
                        )
                    }

                }
        }

    }

    fun getFollowingUserIds(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.getFollowingUserIds(userId) { followingList ->
                _followingIds.value = followingList
            }
        }
    }

    fun checkIfFollowing(followerId: String, followingId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.checkIfFollowing(followerId, followingId) { isFollowing ->
                _followState.value = isFollowing
            }
        }
    }

    fun getFollowingCount(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.getFollowingCountUpdate(userId) { followerCount ->
                _followingCount.postValue(followerCount)
            }
        }
    }

    fun getFollowerCount(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.getFollowerCountUpdate(userId) { followingCount ->
                _followerCount.postValue(followingCount)
            }
        }
    }
}