package com.bapool.bapool
// 정리 필요
// 싱글톤으로 레트로핏 객채 생성 및 인터페이스 정리


import com.bapool.bapool.retrofit.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.auth.model.OAuthToken
import retrofit2.Call
import retrofit2.http.*
import java.net.CookieManager

interface RetrofitService {

    // 정연수
    @GET("/users/kakao")
    fun gettoken(
        @Header("kakoToken") kakoToken: String
    ): Call<accessToken>

    @POST("/users/info/{user-id}")
    fun setUserInfo(
        @Header("Authorization") accessToken: String,
        @Path("user-id") userId: Long,
        @Body request: PostRegisterRequest
    ): Call<PostRegisterResponse>
//--------------------------------------------------------------------------
    // 손승현
    @GET("groups/1")
    fun getResGrpList(
//        @Query("resGrpList") resGrpListApi: ArrayList<ResGrpListModel>
    ): Call<GetResGroupListResponse>
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
        private const val BASE_URL = "https://655c8626-5f5d-4846-b60c-20c52d2ea0da.mock.pstmn.io"


        val client = OkHttpClient.Builder()
//            .cookieJar(JavaNetCookieJar(CookieManager())) //쿠키매니저 연결
            .build()

        fun create(): RetrofitService {

            val gson : Gson = GsonBuilder().setLenient().create()

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
