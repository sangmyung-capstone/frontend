package com.bapool.bapool.retrofit

import com.bapool.bapool.retrofit.data.GetResGroupListResponse
import com.bapool.bapool.retrofit.data.GetRestaurantsResult
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/*** !!! real server !!! ***/
interface ServerRetrofit {

    // 이현제
    @GET("/test/restaurants/{user-id}")
    fun getRestaurants(
        @Path("user-id") userId: Long?,
        @Query("rect") rect: String?,
    ): Call<GetRestaurantsResult>
    //손승현
    @GET("test/parties/{user_id}/{restaurant_id}")
    fun getResGrpList(
        @Path("user_id") userId: Long,
        @Path("restaurant_id") restaurantId: Long,
    ): Call<GetResGroupListResponse>
    //-----------------------------------------------------------------------------

    // 싱글톤 객체 생성
    companion object {
        private const val BASE_URL = "https://myfirstdomain.store"
//        private const val BASE_URL = "https://2c0ecd2a-cbe7-48ce-ac13-4c0a1e451672.mock.pstmn.io"


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