package com.bapool.bapool.ui

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
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

    class CustomItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                left = 0
                right = 0
                bottom = space

                // Add top margin only for the first item to avoid double space between items
                if (parent.getChildLayoutPosition(view) == 0) {
                    top = 0
                }
            }
        }
    }

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
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        binding.recyclerView.addItemDecoration(CustomItemDecoration(spacingInPixels))

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