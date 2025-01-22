package com.example.socialmediaapp.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.socialmediaapp.data.entity.User

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User)

    @Upsert
    suspend fun upsertAllUsers(users: List<User>)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("Select * From User ORDER BY userId ASC")
    fun getAllUser(): LiveData<List<User>>

}