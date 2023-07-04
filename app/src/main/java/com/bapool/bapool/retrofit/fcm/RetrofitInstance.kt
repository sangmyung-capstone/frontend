package com.bapool.bapool.retrofit.fcm

import com.bapool.bapool.retrofit.fcm.Repo.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object {

        private val retrofit by lazy {

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        }

        val api = retrofit.create(NotiAPI::class.java)
    }
}