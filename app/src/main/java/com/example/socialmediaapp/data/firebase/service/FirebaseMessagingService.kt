package com.example.socialmediaapp.data.firebase.service
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.socialmediaapp.R
import com.example.socialmediaapp.other.Constant.COLLECTION_USERS
import com.example.socialmediaapp.ui.acitivity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var db: FirebaseFirestore

    @Inject
    lateinit var auth: FirebaseAuth

    companion object {
        const val TAG = "FirebaseMessagingService"
        val CHANNEL_ID = "social_media_app_channel"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
//        saveTokenToFirestore(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        message.notification.let {
            Log.d(TAG, "Notification Title: ${it?.title}")
            Log.d(TAG, "Notification Body: ${it?.body}")
            showNotification(it?.title, it?.body)
        }

        message.data.let { data ->
            // If the message contains a data payload
            Log.d(TAG, "Data Payload: $data")

            val title = data["title"] ?: "No Title"
            val body = data["body"] ?: "No Body"

            Log.d(TAG, "Message received from: ${message.from}")


            showNotification(title, body)
        }
    }

    private fun showNotification(title: String?, body: String?) {
        Log.d(TAG, "Showing notification with title: $title and body: $body")

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel Description"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_orange)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationId = Random.nextInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun saveTokenToFirestore(token: String) {
        val userId = auth.currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection(COLLECTION_USERS).document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d(TAG, "Token saved successfully!: $token") }
            .addOnFailureListener { Log.e(TAG, "Error saving token", it) }
    }



}