package com.example.socialmediaapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.firebase.remote.FollowerRemoteDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _followState = MutableStateFlow(false)
    val followState: MutableStateFlow<Boolean> = _followState

    private val _followingIds = MutableStateFlow(emptyList<String>())
    val followingIds: MutableStateFlow<List<String>> = _followingIds

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

    fun getFollowerCount(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.getFollowerCountUpdate(userId) { followerCount ->
                _followingCount.postValue(followerCount)
            }
        }
    }
}