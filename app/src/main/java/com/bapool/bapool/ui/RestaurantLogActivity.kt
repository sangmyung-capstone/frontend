package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.RestaurantLogAdapter
import com.bapool.bapool.databinding.ActivityRestaurantLogBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetRestaurantLogResponse
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantLogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRestaurantLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retro = ServerRetrofit.create()
        val RestaurantsLogList = mutableListOf<GetRestaurantLogResponse.parties>()

        retro.GetrestaurantsLog(UserId!!)
            .enqueue(object : Callback<GetRestaurantLogResponse> {
                override fun onResponse(
                    call: Call<GetRestaurantLogResponse>,
                    response: Response<GetRestaurantLogResponse>
                ) {
                    if (response.isSuccessful) {
                        val loglist = response.body()?.result?.parties
                        Log.d("bap", "onResponse 성공 ${response.body()}")
                        // handle successful response
                        loglist?.let { RestaurantsLogList.addAll(it) }
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        // handle error response
                        Log.d("bap", "onResponse 실패 $response")

                    }
                }

                override fun onFailure(call: Call<GetRestaurantLogResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })
        //통신과정\

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RestaurantLogAdapter(RestaurantsLogList)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}