package com.bapool.bapool.ui

import android.content.Intent
import android.os.Build.VERSION_CODES.M
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.RestaurantLogAdapter
import com.bapool.bapool.databinding.ActivityRestaurantLogBinding
import com.bapool.bapool.preference.MyApplication
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetRestaurantLogResponse
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.LoginActivity.Companion.UserToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantLogActivity : AppCompatActivity() {
    //뒤로가기 콜백 선언
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로 버튼 이벤트 처리
            val intent = Intent(this@RestaurantLogActivity, HomeActivity::class.java)
            intent.putExtra("destination", "MypageFragment")
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRestaurantLogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retro = ServerRetrofit.create()
        val RestaurantsLogList = mutableListOf<GetRestaurantLogResponse.parties>()
        UserToken = MyApplication.prefs.getString("prefstoken", "")
        UserId = MyApplication.prefs.getString("prefsid", "").toLong()
        this.onBackPressedDispatcher.addCallback(this, callback) //위에서 생성한 콜백 인스턴스 붙여주기


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