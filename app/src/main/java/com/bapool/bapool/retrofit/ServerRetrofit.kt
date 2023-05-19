package com.bapool.bapool.retrofit

import com.bapool.bapool.retrofit.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
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

    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생성
    companion object {
        private const val BASE_URL = "https://myfirstdomain.store"


        val client = OkHttpClient.Builder()
//            .cookieJar(JavaNetCookieJar(CookieManager())) //쿠키매니저 연결
            .build()

        fun create(): ServerRetrofit {

            val gson: Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                .client(client)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addConverterFactory(ScalarsConverterFactory.create())    // List가 아닌 response 사용 시
                .build()
                .create(ServerRetrofit::class.java)
        }
    }
}