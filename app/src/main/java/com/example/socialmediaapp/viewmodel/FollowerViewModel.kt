package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.firebase.remote.FollowerRemoteDatabase
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.data.room.follower.FollowerRepository
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
    private val readAllDatabase: LiveData<List<Follower>>
    private val followerRepository: FollowerRepository

    init {
        val followerDao = AppDatabase.getInstance(application).followerDao()
        followerRepository = FollowerRepository(followerDao)
        readAllDatabase = followerRepository.readAllDatabase
    }

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

    fun getFollowersOfAnUser(userId: String): LiveData<List<Follower>> {
         return followerRepository.getFollowersByFollowingId(userId)
    }

    fun fetchDataFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val followers = followerRemoteDatabase.getAllFollower()
            followerRepository.upsertFollowers(followers)
        }

    }

    fun getFollowingOfAnUser(userId: String): LiveData<List<Follower>> {
        return followerRepository.getFollowingByFollowerId(userId)

    }

    fun checkIfFollowing(followerId: String, followingId: String): LiveData<Int> {
        return followerRepository.checkIfFollowing(followerId, followingId)
    }

    fun checkIfFollowingChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            followerRemoteDatabase.listenForFollowerChanges {changeType, follower ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("follow", changeType.toString())
                    when (changeType) {
                        ADDED -> {
                            followerRepository.upsertFollower(follower)
                            Log.d("follow", "added: $follower")
                        }
                        REMOVED -> {
                            followerRepository.deleteFollower(follower)
                            Log.d("follow", "deleted: $follower")
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}