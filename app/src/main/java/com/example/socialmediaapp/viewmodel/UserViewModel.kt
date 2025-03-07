package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.remote.UserRemoteDatabase
import com.example.socialmediaapp.data.room.database.AppDatabase
import com.example.socialmediaapp.data.room.user.UserRepository
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

    private val readAllDatabase: LiveData<List<User>>
    private val userRepository: UserRepository

    init {
        val userDao = AppDatabase.getInstance(application).userDao()
        userRepository = UserRepository(userDao)
        readAllDatabase = userRepository.readAllDatabase
        Log.d("view model", "view model created")
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

    fun updateProfilePicture(userId: String, profilePicture: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.handlePfpUpload(userId, profilePicture)
        }
    }

    //* Retrieve Data
    fun deleteAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.deleteAllUser()
        }
    }
    fun getAllUser(): LiveData<List<User>> {
        return readAllDatabase
    }
    fun getUserInfoById(uid: String): LiveData<User> {
        return userRepository.getUserInfoById(uid)
    }
    fun fetchDataFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = userRemoteDatabase.getAllUsers()
            for (user in users) {
                Log.d("view model", "$users")
            }

            userRepository.upsertAllUsers(users)
        }
    }

    fun getUserByUsername(username: String): LiveData<List<User>> {
        return userRepository.getUserByUsername(username)
    }

    fun getUserByName(name: String): LiveData<List<User>> {
        return userRepository.getUserByName(name)
    }


    //TODO fix this function, it doesn't listen for changes
    fun checkIfUserChanges() {
        viewModelScope.launch(Dispatchers.IO) {
            userRemoteDatabase.listenForUsersChanges { changeType, user ->
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d("user_listener", changeType.toString())
                    when (changeType) {
                        ADDED, MODIFIED -> {
                            userRepository.upsertUser(user)
                            Log.d("user_listener", "added: $user")
                        }
                        REMOVED -> {
                            userRepository.deleteUser(user)
                            Log.d("user_listener", "deleted: $user")
                        }
                        else -> {}
                    }                        }
                }


        }

    }

}
