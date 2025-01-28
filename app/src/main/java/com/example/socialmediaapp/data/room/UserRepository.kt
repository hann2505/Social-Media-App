package com.example.socialmediaapp.data.room

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.socialmediaapp.data.entity.User

class UserRepository(
    private val userDao: UserDao
) {
    val readAllDatabase: LiveData<List<User>> = userDao.getAllUser()

    val firstUser: LiveData<User> = userDao.getUserInfoById("9oFq1ENNqgX7bcZ5HJiy0YxAf7t2")

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

}