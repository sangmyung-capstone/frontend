package com.bapool.bapool
// 정리 필요
// 싱글톤으로 레트로핏 객채 생성 및 인터페이스 정리


import com.bapool.bapool.retrofit.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    // 정연수
    @GET("/users/kakao")
    fun gettoken(
        @Header("kakoToken") kakoToken: String,
    ): Call<accessToken>

    @POST("/users/info/{user-id}")
    fun setUserInfo(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: PostRegisterRequest,
    ): Call<PostRegisterResponse>


    @GET("users/mypage/{user-id}")
    fun getMyPage(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long
    ): Call<GetMypageResponse>

    @PATCH("/users/info/{user-id}")
    fun ChangeUserInfo(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: PatchChangeProfileRequest
    ): Call<PatchChangeProfileResponse>

    @DELETE("/users/{user-id}")
    fun DeleteUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long
    ):Call<DeleteUserResponse>

    @GET("/users/block/{user-id}")
    fun GetBlockUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long
    ): Call<GetBlockUserResponse>

    @POST("/users/block/{user-id}")
    fun DelteBlockUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: DeleteBlockUserRequest
    ): Call<DeleteBlockUserResponse>

    @GET("/restaurants/log/{user-id}")
    fun GetrestaurantsLog(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long
    ): Call<GetRestaurantLogResponse>

    //--------------------------------------------------------------------------
    // 손승현
    @GET("groups/{user_id}/{restaurant_id}")
    fun getResGrpList(
        @Path("user_id") userId: Long,
        @Path("restaurant_id") restaurantId: Long
    ): Call<GetResGroupListResponse>

    @POST("groups/{user_id}")
    fun makeGrp(
        @Path("user_id") userId: Long,
        @Body request: PostMakeGrpRequest
    ): Call<PostMakeGrpResponse>

    //--------------------------------------------------------------------------
    // 이현제
    @GET("/restaurants")
    fun getRestaurants(
        @Query("rect") rect: String?
    ): Call<GetRestaurantsResult>

    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생성
    companion object {
        //        private const val BASE_URL = "(your url)"
        private const val BASE_URL = "https://29696102-697c-48a4-ba31-d0470971cc8a.mock.pstmn.io"


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
