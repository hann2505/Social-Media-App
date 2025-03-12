package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.PostWithUser
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.remote.UserRemoteDatabase
import com.example.socialmediaapp.other.FirebaseChangeType
import com.example.socialmediaapp.other.FirebaseChangeType.ADDED
import com.example.socialmediaapp.other.FirebaseChangeType.MODIFIED
import com.example.socialmediaapp.other.FirebaseChangeType.REMOVED
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    application: Application,
    private val userRemoteDatabase: UserRemoteDatabase
) : AndroidViewModel(application) {
    var userLiveData: MutableLiveData<User>? = null

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun fetchUserInfo(userId: String): LiveData<User?> {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRemoteDatabase.fetchUserInfo(userId)
            _user.postValue(user)
        }
        return user
    }

    //* Update
    fun upsertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.upsertUser(user)
        }
    }
    fun updateName(userId: String, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.updateName(userId, name)
        }
    }
    fun updateUsername(userId: String, username: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.updateUsername(userId, username)
        }

    }
    fun updateBio(uid: String, bio: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.updateBio(uid, bio)
        }
    }
    fun updateGender(uid: String, gender: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.updateGender(uid, gender)
        }
    }

    fun updateProfilePicture(userId: String, gender: Boolean, profilePicture: Uri?, setAsDefault: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            if (setAsDefault)
                userRemoteDatabase.setProfilePictureAsDefault(userId, gender)
            else
                userRemoteDatabase.handlePfpUpload(userId, profilePicture)
        }
    }

    fun searchUser(query: String): LiveData<List<User>> {
        val searchResults = MutableLiveData<List<User>>()
        viewModelScope.launch(Dispatchers.IO) {
            val users = userRemoteDatabase.searchUser(query)
            searchResults.postValue(users)
        }
        return searchResults
    }

    fun setProfilePictureAsDefault(userId: String, gender: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.setProfilePictureAsDefault(userId, gender)
        }
    }


    fun checkIfUserChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.listenForUsersChanges { changeType, user ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("user_listener", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            Log.d("user_listener", "added: $user")
                        }
                        REMOVED -> {
                            Log.d("user_listener", "deleted: $user")
                        }
                        else -> {}
                    }                        }
                }


        }

    }

}
