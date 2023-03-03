package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName

data class accessToken(
    @SerializedName("userToken")
    var userToken: String,

    @SerializedName("firstLogin")
    var firstLogin: Int
)
