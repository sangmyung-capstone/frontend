package com.bapool.bapool.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bapool.bapool.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        callAlarm(this, 1, "테스트")
    }

    fun callAlarm(context: Context, alarm_code: Int, content: String) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, MyReceiver::class.java) //리시버로 전달될 인텐트 설정
        receiverIntent.apply {
            putExtra("alarm_rqCode", alarm_code) //요청 코드를 리시버에 전달
            putExtra("content", content) //수정_일정 제목을 리시버에 전달
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                alarm_code,
                receiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                alarm_code,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val calendar = Calendar.getInstance()
        calendar.timeInMillis =
            System.currentTimeMillis() + 60 * 1000 // Adding one minute in milliseconds


        //API 23(android 6.0) 이상(해당 api 레벨부터 도즈모드 도입으로 setExact 사용 시 알람이 울리지 않음)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}