package com.example.socialmediaapp.data.firebase.api

import com.example.socialmediaapp.data.entity.notification.FcmMessage
import com.example.socialmediaapp.other.Constant.PROJECT_ID
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface NotificationAPI {
    @POST("v1/projects/$PROJECT_ID/messages:send")
    suspend fun sendNotification(
        @Header("Authorization") auth: String,
        @Body message: FcmMessage
    ): Response<ResponseBody>
}