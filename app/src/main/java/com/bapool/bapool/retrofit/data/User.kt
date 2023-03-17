package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName

data class accessToken(
    @SerializedName("userToken")
    var userToken: String,

    @SerializedName("firstLogin")
    var firstLogin: Boolean
)

data class PostRegisterResponse(
    val code: Int,
    val message: String,
    @SerializedName("is_duplicate")
    var is_duplicate: Boolean
)

data class PostRegisterRequest(
    var nickname: String,
    var profileImg: Int
)
