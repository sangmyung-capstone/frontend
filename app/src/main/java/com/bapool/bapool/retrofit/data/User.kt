package com.bapool.bapool.retrofit.data

import com.google.gson.annotations.SerializedName

//data class accessToken(
//    @SerializedName("userToken")
//    var userToken: String,
//)

//data class PostRegisterResponse(
//    val code: Int,
//    val message: String,
//    @SerializedName("is_duplicate")
//    var is_duplicate: Boolean
//)

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

data class BlockUserResponse(
    val code: Int,
    val message: String,
)

data class BlockUserRequest(
    val user_id: Long,
)

//네이버 로그인 확인 데이터클래스
data class PostNaverLoginCheckResponse(
    val code: Int,
    val message: String,
    val result: Boolean,
)

data class PostNaverLoginCheckRequest(
    val access_token: String,
)

//네이버 회원가입 데이터 클래스
data class PostNaverSignupRequest(
    val access_token: String,
    var nickname: String,
    var profile_img_id: Int,
)

data class PostNaverSignupResponse(
    val code: Int,
    val message: String,
    val result: PostNaverSignupResponseResult,
) {
    data class PostNaverSignupResponseResult(
        val user_id: Long,
        val access_token: String,
        val refresh_token: String,
    )
}

data class PostNaverSigninRequest(
    val access_token: String,
)

//네이버 로그인 데이터 클래스
data class PostNaverSigninResponse(
    val code: Int,
    val message: String,
    val result: PostNaverSigninResponseResult,
) {
    data class PostNaverSigninResponseResult(
        val user_id: Long,
        val access_token: String,
        val refresh_token: String,
    )
}

//카카오 로그인 확인 데이터클래스
data class PostKakaoLoginCheckResponse(
    val code: Int,
    val message: String,
    val result: Boolean,
)

data class PostkakaoLoginCheckRequest(
    val access_token: String,
)

//네이버 회원가입 데이터 클래스
data class PostKakaoSignupRequest(
    val access_token: String,
    var nickname: String,
    var profile_img_id: Int,
)

data class PostKakaoSignupResponse(
    val code: Int,
    val message: String,
    val result: PostKakaoSignupResponseResult,
) {
    data class PostKakaoSignupResponseResult(
        val user_id: Long,
        val access_token: String,
        val refresh_token: String,
    )
}

data class PostkakaoSigninRequest(
    val access_token: String,
)

//네이버 로그인 데이터 클래스
data class PostKakaoSigninResponse(
    val code: Int,
    val message: String,
    val result: PostKakaoSigninResponseResult,
) {
    data class PostKakaoSigninResponseResult(
        val user_id: Long,
        val access_token: String,
        val refresh_token: String,
    )
}

data class FirebaseUserInfo(
    val imgUrl: String = "",
    val nickName: String = "",
    val bannedUser: List<String>? = null,
    val token: String? = null,
)

data class GetRatingUserResponse(
    val code: Int,
    val message: String,
    val result: GetRatingUserResult
) {
    data class GetRatingUserResult(
        val users: List<GetRatingUserResultUser>
    )

    data class GetRatingUserResultUser(
        val user_id: Long,
        val nickname: String,
    )
}

data class PostRatingUserRequest(
    var users: List<UserData>
)

data class UserData(
    val user_id: Long,
    val rating: Float,
    val hashtag: List<Int>
)

data class PostRatingUserResponse(
    val code: Int,
    val message: String,
)



