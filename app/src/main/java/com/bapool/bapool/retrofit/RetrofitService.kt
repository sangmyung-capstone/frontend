package com.bapool.bapool

import com.bapool.bapool.retrofit.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.*

/*** !!! MOCK SERVER !!! ***/
interface RetrofitService {

    // 정연수
    @POST("/users/naver/signin/already")
    fun PostNaverLoginCheck(
        @Body request: PostNaverLoginCheckRequest
    ): Call<PostNaverLoginCheckResponse>

    @POST("/users/naver/signup")
    fun PostNaverSignup(
        @Body request: PostNaverSignupRequest
    ): Call<PostNaverSignupResponse>

    @POST("/users/naver/signin")
    fun PostNaverSingin(
        @Body request: PostNaverSigninRequest
    ): Call<PostNaverSigninResponse>

    @POST("/users/kakao/signin/already")
    fun PostKakaoLoginCheck(
        @Body request: PostkakaoLoginCheckRequest
    ): Call<PostKakaoLoginCheckResponse>

    @POST("/users/kakao/signup")
    fun PostKakaoSignup(
        @Body request: PostKakaoSignupRequest
    ): Call<PostKakaoSignupResponse>

    @POST("/users/kakao/signin")
    fun PostKakaoSingin(
        @Body request: PostkakaoSigninRequest
    ): Call<PostKakaoSigninResponse>

    @GET("users/mypage/{user-id}")
    fun getMyPage(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
    ): Call<GetMypageResponse>

    @PATCH("/users/info/{user-id}")
    fun ChangeUserInfo(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: PatchChangeProfileRequest,
    ): Call<PatchChangeProfileResponse>

    @DELETE("/users/{user-id}")
    fun DeleteUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
    ): Call<DeleteUserResponse>

    @GET("/users/block/{user-id}")
    fun GetBlockUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
    ): Call<GetBlockUserResponse>

    @POST("/users/block/{user-id}")
    fun BlockUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: BlockUserRequest,
    ): Call<BlockUserResponse>

    @GET("/restaurants/log/{user-id}")
    fun GetrestaurantsLog(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
    ): Call<GetRestaurantLogResponse>

    @GET("/users/evaluate/{user-id}")
    fun GetEvaluateUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Query("party-id") partyId: Long
    ): Call<GetEvaluateUserResponse>


    //--------------------------------------------------------------------------
    // 손승현
    @GET("parties/{user_id}/{restaurant_id}")
    fun getResGrpList(
        @Path("user_id") userId: Long,
        @Path("restaurant_id") restaurantId: Long,
    ): Call<GetResPartyListResponse>
    //http://dev.scrap-j2kb.shop/test/parties/1/1470337852
    @POST("parties/{user_id}")
    fun makeGrp(
        @Path("user_id") userId: Long,
        @Body request: PostMakePartyRequest,
    ): Call<PostMakePartyResponse>

    //--------------------------------------------------------------------------
    // 이현제
    @GET("/restaurants/{user-id}")
    fun getRestaurants(
        @Path("user-id") userId: Long?,
        @Query("rect") rect: String?,
    ): Call<GetRestaurantsResult>

    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생
    companion object {
        private const val BASE_URL = "https://2c0ecd2a-cbe7-48ce-ac13-4c0a1e451672.mock.pstmn.io"

        val client = OkHttpClient.Builder()
//            .cookieJar(JavaNetCookieJar(CookieManager())) //쿠키매니저 연결
            .build()

        fun create(): RetrofitService {

            val gson: Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())    // List가 아닌 response 사용 시
                .build()
                .create(RetrofitService::class.java)

        }

    }
}
