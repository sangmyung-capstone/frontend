package com.bapool.bapool.retrofit

import com.bapool.bapool.retrofit.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException

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

    @GET("/users/evaluate/{user-id}")
    fun GetEvaluateUser(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Query("party-id") partyId: Long
    ): Call<GetEvaluateUserResponse>

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
  
  //--------------------------------------------------------------------------

    //손승현
    @GET("test/parties/{user_id}/{restaurant_id}")
    fun getResGrpList(
        @Path("user_id") userId: Long,
        @Path("restaurant_id") restaurantId: Long,
    ): Call<GetResGroupListResponse>

    @POST("test/parties/{user_id}")
    fun makeGrp(
        @Path("user_id") userId: Long,
        @Body request: PostMakePartyRequest,
    ): Call<PostMakePartyResponse>

    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생성
    companion object {
        private const val BASE_URL = "https://myfirstdomain.store"
//        private const val BASE_URL = "https://2c0ecd2a-cbe7-48ce-ac13-4c0a1e451672.mock.pstmn.io"


//         헤더 추가
//        private fun client(interceptor: AppInterceptor): OkHttpClient = OkHttpClient.Builder().run {
//            addInterceptor(interceptor)
//            build()
//        }
//
//        class AppInterceptor : Interceptor {
//            @Throws(IOException::class)
//            override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
//                val newRequest = request().newBuilder()
//                    .addHeader("header key", "header value")
//                    .build()
//                proceed(newRequest)
//            }
//        }

        private val client = OkHttpClient.Builder()
//            .cookieJar(JavaNetCookieJar(CookieManager())) //쿠키매니저 연결
            .build()

        fun create(): ServerRetrofit {

            val gson: Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
//                .client(client(AppInterceptor()))
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())    // List가 아닌 response 사용 시
                .build()
                .create(ServerRetrofit::class.java)
        }
    }
}