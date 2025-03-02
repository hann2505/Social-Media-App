package com.example.socialmediaapp.extensions

import java.text.SimpleDateFormat
import java.util.Date

object TimeConverter {

    private const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000

    private fun getDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    private fun dateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    }

    private fun timeFormatter(): SimpleDateFormat {
        return SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        return dateFormatter().format(getDate(timestamp))
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        return timeFormatter().format(getDate(timestamp))
    }

    private fun hasOnDayGap(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp >= ONE_DAY_IN_MILLIS
    }

    fun convertTimestampToDateTime(timestamp: Long): String {
        return if (hasOnDayGap(timestamp)) {
            convertTimestampToDate(timestamp)
        } else {
            convertTimestampToTime(timestamp)
        }
    }

}