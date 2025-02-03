package com.example.socialmediaapp.data.room.user

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.socialmediaapp.data.entity.User

@Database(
    entities = [User::class],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: UserDatabase?= null

        fun getInstance(context: Context) : UserDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "social_media_app_db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }



}