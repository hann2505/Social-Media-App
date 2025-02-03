package com.example.socialmediaapp.data.room.user

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

    @Query("DELETE FROM User")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM User ORDER BY userId ASC")
    fun getAllUser(): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE userId = :userId")
    fun getUserInfoById(userId: String): LiveData<User>

    @Query("SELECT * FROM User WHERE userName LIKE '%' || :username || '%'")
    fun getUserByUsername(username: String): LiveData<List<User>>

    @Query("SELECT * FROM User WHERE name LIKE '%' || :name || '%'")
    fun getUserByName(name: String): LiveData<List<User>>

}