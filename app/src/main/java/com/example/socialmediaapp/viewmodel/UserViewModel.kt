package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.remote.UserRemoteDatabase
import com.example.socialmediaapp.data.room.user.UserDatabase
import com.example.socialmediaapp.data.room.user.UserRepository
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
        val userDao = UserDatabase.getInstance(application).userDao()
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

}