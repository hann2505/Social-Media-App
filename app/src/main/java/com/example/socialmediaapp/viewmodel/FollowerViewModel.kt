package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private val _followers = MutableLiveData<List<Follower>>()
    val followers: LiveData<List<Follower>> = _followers

    private val _followerCount = MutableLiveData<Int>()
    val followerCount: LiveData<Int> = _followerCount

    private val _followingCount = MutableLiveData<Int>()
    val followingCount: LiveData<Int> = _followingCount

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

    fun getFollowerCount(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.getFollowerCountUpdate(userId) { followerCount ->
                _followerCount.postValue(followerCount)
            }
        }
    }
}