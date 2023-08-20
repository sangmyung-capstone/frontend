package com.bapool.bapool.ui

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bapool.bapool.R
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetRatingUserResponse
import com.bapool.bapool.retrofit.data.PatchPartyDoneResponse
import com.bapool.bapool.retrofit.fcm.NotiModel
import com.bapool.bapool.retrofit.fcm.PushNotification
import com.bapool.bapool.retrofit.fcm.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RatingReceiver() : BroadcastReceiver() {

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
        val userid = intent.extras!!.getLong("userid")
        val partyid = intent.extras!!.getInt("partyid")
        val realpartyid = intent.extras!!.getInt("realpartyid")
        val groupname = intent.extras!!.getString("group_name")
        val items = intent.extras!!.getStringArrayList("key_list")


        val retro = ServerRetrofit.create()
        retro.PatchPartyDone(userid, realpartyid.toLong())
            .enqueue(object : Callback<PatchPartyDoneResponse> {
                override fun onResponse(
                    call: Call<PatchPartyDoneResponse>,
                    response: Response<PatchPartyDoneResponse>
                ) {
                    if (response.isSuccessful) {
                        intent2.putExtra("Activity", "Rating")

                    } else {
                        // handle error response
                        Log.d("bap", "onResponse 실패 $response")

                    }
                }

                override fun onFailure(call: Call<PatchPartyDoneResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })

        if (items != null) {
            for (data in items) {
                sendNotificationFcm(groupname.toString(), data, title.toString(), requestCode)
            }
        }

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

        val notification = builder.setContentTitle(title)
            .setContentText("$title")
            .setSmallIcon(R.drawable.bapool)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        Log.d("알림", "$notification")

        manager.notify(requestCode, notification)
    }

    fun sendNotificationFcm(
        groupname: String,
        firebaseToken: String,
        notificationText: String,
        requestCode: Int
    ) {

        val notiModel = NotiModel(groupname, notificationText, (requestCode/1000).toString(),
            LoginActivity.UserId.toString(), LoginActivity.UserToken.toString(), requestCode)

        val pushModel = PushNotification(notiModel, firebaseToken)

        fcmPush(pushModel)
    }

    //fcm 보내기
    private fun fcmPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        RetrofitInstance.api.postNotification(notification)
    }
}

