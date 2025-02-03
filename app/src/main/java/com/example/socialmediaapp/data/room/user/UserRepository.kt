package com.example.socialmediaapp.data.room.user

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.User

class UserRepository(
    private val userDao: UserDao
) {
    val readAllDatabase: LiveData<List<User>> = userDao.getAllUser()

    suspend fun upsertUser(user: User) {
        userDao.upsertUser(user)
    }

    suspend fun upsertAllUsers(users: List<User>) {
        userDao.upsertAllUsers(users)
    }

    suspend fun deleteAllUser() {
        userDao.deleteAllUsers()
    }

    fun getUserInfoById(userId: String): LiveData<User> {
        Log.d("view model", "get user info by id: ${userDao.getUserInfoById(userId).value}")
        return userDao.getUserInfoById(userId)
    }

    fun getUserByUsername(username: String): LiveData<List<User>> {
        return userDao.getUserByUsername(username)
    }

    fun getUserByName(name: String): LiveData<List<User>> {
        return userDao.getUserByName(name)
    }

}