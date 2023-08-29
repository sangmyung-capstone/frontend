package com.bapool.bapool.retrofit.fcm


data class NotiModel(
    val title: String = "",
    val content: String = "",
    val partyId: String = "",
    val userId: String = "",
    val userToken: String = "",
    var alarm_code: Int = 123,
    var category_code: Int = 1 //1이면 채팅 2면 식전알람 3이면 유저평가
)

