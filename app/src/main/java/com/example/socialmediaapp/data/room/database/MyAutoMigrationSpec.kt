package com.example.socialmediaapp.data.room.database

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec


@DeleteColumn(tableName = "User", columnName = "followers")
@DeleteColumn(tableName = "User", columnName = "following")
@DeleteColumn(tableName = "User", columnName = "posts")
class MyAutoMigrationSpec : AutoMigrationSpec {
}