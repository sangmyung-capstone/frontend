package com.bapool.bapool.retrofit.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bapool.bapool.R

import com.bapool.bapool.ui.ChattingAndPartyInfoMFActivity
import com.bapool.bapool.ui.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.e(TAG, message.notification?.title.toString())
        Log.e(TAG, message.notification?.body.toString())
        Log.e(TAG, message.data["title"].toString())
        Log.e(TAG, message.data["content"].toString())

//        val title = message.notification?.title.toString()
//        val body = message.notification?.body.toString()

        val title = message.data["title"].toString()
        val body = message.data["content"].toString()
        val partyId = message.data["partyId"].toString()
        val userId = message.data["userId"].toString()
        val userToken = message.data["userToken"].toString()
        var alarm_code = message.data["alarm_code"]

        val intent = Intent(applicationContext, ChattingAndPartyInfoMFActivity::class.java)
        intent.putExtra("partyId", partyId)
        intent.putExtra("notificationUserId", userId)
        intent.putExtra("notificationUserToken", userToken)
        intent.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE//일회용 펜딩 인텐트
        )


        createNotificationChannel()
        sendNotification(title, body, pendingIntent, alarm_code!!.toInt())

    }


    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "name"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("Test_Channel", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendNotification(
        title: String,
        body: String,
        pendingIntent: PendingIntent,
        alarm_code: Int = 123
    ) {
        if (alarm_code != 123) {
            val intent = Intent(this, LoginActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(
                    this,
                    alarm_code,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
                )

            val builder = NotificationCompat.Builder(this, "Test_Channel")
                .setSmallIcon(R.drawable.bapool)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(alarm_code, builder.build())
            }
        }

        var builder = NotificationCompat.Builder(this, "Test_Channel")
            .setSmallIcon(R.drawable.bapool)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(alarm_code, builder.build())
        }
    }


//
//    class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//        override fun onNewToken(token: String) {
//            super.onNewToken(token)
//
//            // Update the token in your app's server or perform any additional logic
//
//            // Example: Print the refreshed token
//            println("Refreshed FCM Token: $token")
//        }
//    }
}
