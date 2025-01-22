package com.example.socialmediaapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.firebase.remote.UserRemoteDatabase
import com.example.socialmediaapp.data.room.UserDatabase
import com.example.socialmediaapp.data.room.UserRepository
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
        fetchDataFromFirebase()
        readAllDatabase = userRepository.readAllDatabase
        Log.d("view model", "view model created")
    }

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.upsertUser(user)
        }
    }

    fun getAllUser() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.readAllDatabase
        }
    }

    private fun fetchDataFromFirebase() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = userRemoteDatabase.getAllUsers()
            for (user in users) {
                Log.d("view model", "$users")
            }

            userRepository.upsertAllUsers(users)
        }
    }

}