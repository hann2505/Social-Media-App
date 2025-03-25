package com.example.socialmediaapp.data.entity.notification

data class PushNotification (
    val topic: String,
    val notification: NotificationData,
    val data: NotificationData
)