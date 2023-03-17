package com.bapool.bapool.retrofit.data

import java.time.LocalDateTime

data class GetResGroupListResponse(
    val restaurant_name: String,
    val groups: List<ResGroupList>
)

data class MyGrpListModel(
    val grpId: Long,
    val resName: String = "",
    val grpName: String = "",
    val participants: Int,
    val max_people: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val lastChat: String = "",
    val notReadChat: Int
)

data class ResGroupList(
    val group_id: Int,
    val grpName: String ,
    val participants: Int,
    val max_people: Int,
    val start_date: String,
    val end_date: String,
    val menu: String,
    val hashtag: ArrayList<String>,
    val detail: String,
    val has_block_user: Boolean,
    val rating: ArrayList<Double>
)