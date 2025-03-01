package com.example.socialmediaapp.data.room.database

import androidx.room.DeleteColumn
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec


//@DeleteColumn(tableName = "User", columnName = "followers")
//@DeleteColumn(tableName = "User", columnName = "following")
//@DeleteColumn(tableName = "User", columnName = "posts")
//@RenameColumn(tableName = "PostMedia", fromColumnName = "imageUrl", toColumnName = "mediaUrl")
//@DeleteColumn(tableName = "Post Medias", columnName = "imageUrl")
@RenameTable(fromTableName = "Post Medias", toTableName = "PostMedia")
@RenameColumn.Entries(
    RenameColumn(tableName = "PostMedia", fromColumnName = "imageUrl", toColumnName = "mediaUrl")
)
class MyAutoMigrationSpec : AutoMigrationSpec {
}