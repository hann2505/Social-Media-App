package com.example.socialmediaapp.data.room

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

}