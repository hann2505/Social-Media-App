package com.example.socialmediaapp.data.room.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.room.post.PostDao
import com.example.socialmediaapp.data.room.user.UserDao
import com.example.socialmediaapp.other.MediaTypeConverter

@Database(
    entities = [
        Post::class,
        User::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = MyAutoMigrationSpec::class)
    ]
)
@TypeConverters(MediaTypeConverter::class) // Register the converter
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase?= null

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "social_media_app_db"
                ).fallbackToDestructiveMigration()
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}