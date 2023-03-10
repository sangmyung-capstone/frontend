package com.bapool.bapool.retrofit.data

import java.time.LocalDateTime

data class MyGroupListModel(
    val grpId: Long,
    val resName: String = "",
    val grpName: String = "",
    val participantNum: Int,
    val deadlineNum: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val lastChat: String = "",
    val notReadChat: Int
)