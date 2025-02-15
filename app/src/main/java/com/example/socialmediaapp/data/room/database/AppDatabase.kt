package com.example.socialmediaapp.data.room.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.room.Comment.CommentDao
import com.example.socialmediaapp.data.room.follower.FollowerDao
import com.example.socialmediaapp.data.room.post.PostDao
import com.example.socialmediaapp.data.room.user.UserDao
import com.example.socialmediaapp.other.MediaTypeConverter

@Database(
    entities = [
        Post::class,
        User::class,
        Follower::class,
        Comment::class
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 3, to = 4)
    ]
)
@TypeConverters(MediaTypeConverter::class) // Register the converter
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun followerDao(): FollowerDao
    abstract fun commentDao(): CommentDao

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