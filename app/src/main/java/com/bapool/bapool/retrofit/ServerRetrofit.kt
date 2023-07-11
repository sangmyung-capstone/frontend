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



    //--------------------------------------------------------------------------

    // 이현제
    @GET("/test/restaurants/{user-id}")
    fun getRestaurants(
        @Path("user-id") userId: Long?,
        @Query("rect") rect: String?,
    ): Call<GetRestaurantsResult>

//    @GET("/restaurants/{user-id}/{restaurant-id}")
//    fun getRestaurantInfo(
//        @Path("user-id") userId: Long?,
//        @Path("restaurant-id") restaurantId: Int?,
//    )
    @GET("/test/restaurants/{user-id}/{restaurant-id}")
    fun getRestaurantInfo(
        @Path("user-id") userId: Long?,
        @Path("restaurant-id") restaurantId: Long?,
        @Query("longitude") longitude: Double?,
        @Query("latitude") latitude: Double?,
    ): Call<GetRestaurantInfoResult>

    //--------------------------------------------------------------------------

    //손승현
    @GET("test/parties/{user_id}/{restaurant_id}")
    fun getResPartyList(
        @Path("user_id") userId: Long,
        @Path("restaurant_id") restaurantId: Long,
    ): Call<GetResPartyListResponse>

    @POST("test/parties/{user-id}")
    fun makeParty(
        @Path("user-id") userId: Long,
        @Body request: PostMakePartyRequest,
    ): Call<PostMakePartyResponse>

    @PATCH("test/parties/{user-id}")
    fun editParty(
        @Path("user-id") userId: Long,
        @Body request: PatchEditPartyInfoRequest,
    ): Call<PatchEditPartyInfoResponse>


    @DELETE("/test/parties/{user-id}/{party-id}")
    fun recessionParty(
        @Path("user-id") userId: Long,
        @Path("party-id") partyId: Long,
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