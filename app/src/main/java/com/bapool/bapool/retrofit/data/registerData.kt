package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName


data class UserInfoResponse(
    val code: Int,
    val message: String,
    @SerializedName("is_duplicate")
    var is_duplicate: Boolean
)

data class UserInfoRequest(
    var nickname: String,
    var profileImg: Int
)