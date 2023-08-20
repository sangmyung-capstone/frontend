package com.bapool.bapool.retrofit.fcm


data class NotiModel(
    val title: String = "",
    val content: String = "",
    val partyId: String = "",
    val userId: String = "",
    val userToken: String = "",
    var alarm_code: Int = 123
)

