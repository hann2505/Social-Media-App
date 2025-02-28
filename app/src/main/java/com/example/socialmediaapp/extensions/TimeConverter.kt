package com.example.socialmediaapp.extensions

import java.text.SimpleDateFormat
import java.util.Date

object TimeConverter {

    private const val oneDayInMillis = 24 * 60 * 60 * 1000

    private fun getDate(timestamp: Long): Date {
        return java.util.Date(timestamp)
    }

    private fun dateFormatter(): SimpleDateFormat {
        return java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    }

    private fun timeFormatter(): SimpleDateFormat {
        return java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        return dateFormatter().format(getDate(timestamp))
    }

    private fun convertTimestampToTime(timestamp: Long): String {
        return timeFormatter().format(getDate(timestamp))
    }

    private fun hasOnDayGap(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp >= oneDayInMillis
    }

    fun convertTimestampToDateTime(timestamp: Long): String {
        return if (hasOnDayGap(timestamp)) {
            convertTimestampToDate(timestamp)
        } else {
            convertTimestampToTime(timestamp)
        }
    }

}