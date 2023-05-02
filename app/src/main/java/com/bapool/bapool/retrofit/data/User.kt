package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName

data class accessToken(
    @SerializedName("userToken")
    var userToken: String,

    @SerializedName("firstLogin")
    var firstLogin: Boolean,
)

data class PostRegisterResponse(
    val code: Int,
    val message: String,
    @SerializedName("is_duplicate")
    var is_duplicate: Boolean,
)

data class PostRegisterRequest(
    var nickname: String,
    var profileImg: Int,
)

data class PatchChangeProfileRequest(
    var nickname: String,
    var profileImg: Int,
)

data class PatchChangeProfileResponse(
    val code: Int,
    val message: String,
    @SerializedName("is_duplicate")
    var is_duplicate: Boolean,
)

data class GetMypageResponse(
    var code: Int,
    val message: String,
    var result: MyPageResult,
) {
    data class MyPageResult(
        val nickname: String,
        val profileImg: Int,
        val rating: Double,
        val hashtag: List<Int>,
    )
}


data class DeleteUserResponse(
    var code: Int,
    var message: String,
)

data class GetBlockUserResponse(
    val code: Int,
    val message: String,
    val result: Result,
) {
    data class Result(
        val users: List<BlockedUser>,
    )

    data class BlockedUser(
        val user_id: Long,
        val nickname: String,
        val block_date: String,
    )
}

data class DeleteBlockUserResponse(
    val code: Int,
    val message: String,
)

data class DeleteBlockUserRequest(
    val user_id: Long,
)

data class FirebaseUserInfo(
    val imageUrl: String = "",
    val nickname: String = "",
    val banned_users: List<String>? = null
)



