package com.bapool.bapool


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import com.google.gson.annotations.SerializedName
import com.kakao.sdk.auth.model.OAuthToken
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface kakaoUser {
    @GET("users/kakao")
    fun gettoken(
        @Header("kakoToken") kakoToken: OAuthToken?
    ): Call<accessToken>
}

data class accessToken (
    @SerializedName("userToken")
    var userToken: String,

    @SerializedName("firstLogin")
    var firstLogin: Int
)


object ApiClient {
    private const val BASE_URL = "(your url)"
    fun getApiClient(token: Any): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(AppInterceptor(token)))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun provideOkHttpClient(interceptor: AppInterceptor): OkHttpClient =
        OkHttpClient.Builder().run {
            addInterceptor(interceptor)
            build()
        }

    class AppInterceptor(private val token: Any) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response = with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("(kakaotoken)", "$token")
                .build()
            proceed(newRequest)
        }
    }
}