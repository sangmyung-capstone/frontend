package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ResGrpListModel(

    @SerializedName("group_id")
    val group_id: Int,
    @SerializedName("group_name")
    val grpName: String ,
    @SerializedName("participants")
    val participants: Int,
    @SerializedName("max_people")
    val max_people: Int,
    @SerializedName("start_date")
    val start_date: String,
    @SerializedName("end_date")
    val end_date: String,
    @SerializedName("menu")
    val menu: String,
    @SerializedName("hashtag")
    val hashtag: ArrayList<String>,
    @SerializedName("detail")
    val detail: String,
    @SerializedName("has_block_user")
    val has_block_user: Boolean,
    @SerializedName("rating")
    val rating: ArrayList<Double>
)
