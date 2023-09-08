package com.bapool.bapool.retrofit

import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserToken
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/*** !!! real server !!! ***/
interface ServerRetrofit {

    // 정연수
    @POST("/auth/naver/signin/already")
    fun PostNaverLoginCheck(
        @Body request: PostNaverLoginCheckRequest,
    ): Call<PostNaverLoginCheckResponse>

    @POST("/auth/naver/signup")
    fun PostNaverSignup(
        @Body request: PostNaverSignupRequest,
    ): Call<PostNaverSignupResponse>

    @POST("/auth/naver/signin")
    fun PostNaverSingin(
        @Body request: PostNaverSigninRequest,
    ): Call<PostNaverSigninResponse>

    @POST("/auth/kakao/signin/already")
    fun PostKakaoLoginCheck(
        @Body request: PostkakaoLoginCheckRequest,
    ): Call<PostKakaoLoginCheckResponse>

    @POST("/auth/kakao/signup")
    fun PostKakaoSignup(
        @Body request: PostKakaoSignupRequest,
    ): Call<PostKakaoSignupResponse>

    @POST("/auth/kakao/signin")
    fun PostKakaoSingin(
        @Body request: PostkakaoSigninRequest,
    ): Call<PostKakaoSigninResponse>

    @GET("users/mypage/{user-id}")
    fun getMyPage(
        @Path("user-id") userId: Long,
    ): Call<GetMypageResponse>

    @DELETE("/users/delete/{user-id}")
    fun DeleteUser(
        @Path("user-id") userId: Long,
    ): Call<DeleteUserResponse>

    @GET("/users/block/{user-id}")
    fun GetBlockUser(
        @Path("user-id") userId: Long,
    ): Call<GetBlockUserResponse>

    @POST("/users/block/{user-id}")
    fun PostBlockUser(
        @Path("user-id") userId: Long,
        @Body request: BlockUserRequest,
    ): Call<BlockUserResponse>

    @GET("/parties/log/{user-id}")
    fun GetrestaurantsLog(
        @Path("user-id") userId: Long,
    ): Call<GetRestaurantLogResponse>

    @GET("/users/rating/{user-id}")
    fun GetRatingUser(
        @Path("user-id") userId: Long,
        @Query("party_id") partyId: Long
    ): Call<GetRatingUserResponse>

    @POST("/users/rating/{user-id}")
    fun PostRatingUser(
        @Path("user-id") userId: Long,
        @Query("party_id") partyId: Long,
        @Body request: PostRatingUserRequest
    ): Call<PostRatingUserResponse>

    @PATCH("/users/info/{user-id}")
    fun ChangeUserInfo(
        @Path("user-id") userId: Long,
        @Body request: PatchChangeProfileRequest,
    ): Call<PatchChangeProfileResponse>

    @PATCH("/parties/done/{user-id}/{party-id}")
    fun PatchPartyDone(
        @Path("user-id") userId: Long,
        @Path("party-id") partyId: Long
    ): Call<PatchPartyDoneResponse>


    //--------------------------------------------------------------------------

    // 이현제
    @GET("/restaurants/{user-id}")
    fun getRestaurants(
        @Path("user-id") userId: Long?,
        @Query("rect") rect: String?,
    ): Call<GetRestaurantsResult>

    @GET("/restaurants/{user-id}/{restaurant-id}")
    fun getRestaurantInfo(
        @Path("user-id") userId: Long?,
        @Path("restaurant-id") restaurantId: Long?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?,
    ): Call<GetRestaurantInfoResult>

    @POST("/restaurants/bottomlist/{user-id}")  // 식당바텀리스트의 이미지 요청
    fun getRestaurantsBottom(
        @Path("user-id") userId: Long?,
        @Body request: GetRestaurantsBottomRequest,
    ): Call<GetRestaurantsBottomResult>

    @GET("/restaurants/search/{user-id}")
    fun getRestaurantsSearch(
        @Path("user-id") userId: Long?,
        @Query("q") q: String?,
//        @Query("rect") rect: String,
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double
    ): Call<GetSearchResult>

    //--------------------------------------------------------------------------

    //손승현
    @GET("/parties/{user-id}/{restaurant-id}")
    fun getResPartyList(
        @Path("user-id") userId: Long,
        @Path("restaurant-id") restaurantId: Long,
    ): Call<GetResPartyListResponse>

    @POST("/parties/{user-id}")
    fun makeParty(
        @Path("user-id") userId: Long,
        @Body request: PostMakePartyRequest,
    ): Call<PostMakePartyResponse>

    @PATCH("/parties/{user-id}")
    fun editParty(
        @Path("user-id") userId: Long,
        @Body request: PatchEditPartyInfoRequest,
    ): Call<PatchEditPartyInfoResponse>

    @DELETE("/parties/{user-id}/{party-id}")
    fun recessionParty(
        @Path("user-id") userId: Long,
        @Path("party-id") partyId: Long,
    ): Call<PatchEditPartyInfoResponse>

    @GET("/users/profile/{user-id}/{other-user-id}")
    fun checkUserProfile(
        @Path("user-id") userId: Long,
        @Path("other-user-id") otherUserId: Long,

        ): Call<CheckUserProfileResponse>

    @POST("/users/block/{user-id}")
    fun BlockUser(
        @Path("user-id") userId: Long,
        @Body request: BlockUserRequest,
    ): Call<BlockUserChattingProfileResponse>

    @PATCH("/parties/close/{user-id}/{party-id}")
    fun closeParty(
        @Path("user-id") userId: Long,
        @Path("party-id") partyId: Long,
    ): Call<PatchEditPartyInfoResponse>

    @PATCH("/parties/change/{user-id}/{party-id}/{other-user-id}")
    fun changePartyLeader(
        @Path("user-id") userId: Long,
        @Path("party-id") partyId: Long,
        @Path("other-user-id") otherUserId: Long
    ): Call<PatchEditPartyInfoResponse>

    @POST("/parties/participant/{user-id}")
    fun participateParty(
        @Path("user-id") userId: Long,
        @Body request: participateParty
    ): Call<PatchEditPartyInfoResponse>


    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생성
    companion object {
        private const val BASE_URL = "https://myfirstdomain.store"

        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val modifiedRequest = originalRequest.newBuilder()
                    .header("ACCESS-TOKEN", UserToken.toString())
                    .build()
                chain.proceed(modifiedRequest)
            }
            // .cookieJar(JavaNetCookieJar(CookieManager())) // 쿠키매니저 연결
            .build()

        fun create(): ServerRetrofit {

            val gson: Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                // .client(client(AppInterceptor()))
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                // .addConverterFactory(ScalarsConverterFactory.create())    // List가 아닌 response 사용 시
                .build()
                .create(ServerRetrofit::class.java)
        }
    }
}