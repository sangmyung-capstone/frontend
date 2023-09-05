package com.bapool.bapool.receiver

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bapool.bapool.R
import com.bapool.bapool.retrofit.fcm.NotiModel
import com.bapool.bapool.retrofit.fcm.PushNotification
import com.bapool.bapool.retrofit.fcm.RetrofitInstance
import com.bapool.bapool.ui.LoginActivity
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.LoginActivity.Companion.UserToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyReceiver() : BroadcastReceiver() {

    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    //오레오 이상은 반드시 채널을 설정해줘야 Notification 작동함
    companion object {
        const val CHANNEL_ID = "channel"
        const val CHANNEL_NAME = "channel1"
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context?, intent: Intent?) {

        manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //NotificationChannel 인스턴스를 createNotificationChannel()에 전달하여 앱 알림 채널을 시스템에 등록
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val intent2 = Intent(context, LoginActivity::class.java)
        val requestCode = intent?.extras!!.getInt("alarm_rqCode")
        val title = intent.extras!!.getString("content")
        val groupname = intent.extras!!.getString("group_name")
        val items = intent.extras!!.getStringArrayList("key_list")

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                requestCode,
                intent2,
                PendingIntent.FLAG_MUTABLE
            ) //Activity를 시작하는 인텐트 생성
        } else {
            PendingIntent.getActivity(
                context,
                requestCode,
                intent2,
                PendingIntent.FLAG_MUTABLE
            )
        }

        if (items != null) {
            for (data in items) {
                sendNotificationFcm(groupname.toString(), data, title.toString(), requestCode)
            }
        }
    }

    fun sendNotificationFcm(
        groupname: String,
        firebaseToken: String,
        notificationText: String,
        requestCode: Int
    ) {

        val notiModel = NotiModel(groupname, notificationText, requestCode.toString(),
            UserId.toString(), UserToken.toString(), requestCode,2)

        val pushModel = PushNotification(notiModel, firebaseToken)

        fcmPush(pushModel)
    }

    //fcm 보내기
    private fun fcmPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }
}