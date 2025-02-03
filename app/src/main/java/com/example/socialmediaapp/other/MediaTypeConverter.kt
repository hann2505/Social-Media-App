package com.example.socialmediaapp.other

import androidx.room.TypeConverter
import com.example.socialmediaapp.data.entity.MediaType

class MediaTypeConverter {

    @TypeConverter
    fun fromMediaType(mediaType: MediaType): String {
        return mediaType.name // Convert ENUM to String
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return try {
            MediaType.valueOf(value) // Convert String back to ENUM
        } catch (e: IllegalArgumentException) {
            MediaType.TEXT
        }
    }
}
