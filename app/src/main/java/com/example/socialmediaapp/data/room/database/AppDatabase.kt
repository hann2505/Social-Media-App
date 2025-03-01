package com.example.socialmediaapp.data.room.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.socialmediaapp.data.entity.Comment
import com.example.socialmediaapp.data.entity.CommentLike
import com.example.socialmediaapp.data.entity.Follower
import com.example.socialmediaapp.data.entity.Post
import com.example.socialmediaapp.data.entity.PostLike
import com.example.socialmediaapp.data.entity.PostMedia
import com.example.socialmediaapp.data.entity.User
import com.example.socialmediaapp.data.room.Comment.CommentDao
import com.example.socialmediaapp.data.room.follower.FollowerDao
import com.example.socialmediaapp.data.room.like.LikeDao
import com.example.socialmediaapp.data.room.media.PostMediaDao
import com.example.socialmediaapp.data.room.post.PostDao
import com.example.socialmediaapp.data.room.user.UserDao
import com.example.socialmediaapp.other.MediaTypeConverter

@Database(
    entities = [
        Post::class,
        User::class,
        Follower::class,
        Comment::class,
        PostLike::class,
        CommentLike::class,
        PostMedia::class
    ],
    version = 7,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 6, to = 7)
    ]
)
@TypeConverters(MediaTypeConverter::class) // Register the converter
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun userDao(): UserDao
    abstract fun followerDao(): FollowerDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao
    abstract fun postMediaDao(): PostMediaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase?= null

//        private val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(db: SupportSQLiteDatabase) {
//                // Example: Add a new column
//                db.execSQL("ALTER TABLE `Post Medias` ADD COLUMN media_url TEXT")
//            }
//        }

        fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "social_media_app_db"
                )
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}