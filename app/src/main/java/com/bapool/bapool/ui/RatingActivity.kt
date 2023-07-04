package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.RatingUserAdapter
import com.bapool.bapool.databinding.ActivityRatinguserBinding
import com.bapool.bapool.retrofit.data.GetRatingUserResponse
import com.bapool.bapool.retrofit.data.PostRatingUserRequest
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RatingActivity : AppCompatActivity() {
    private var _binding: ActivityRatinguserBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRatinguserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retro = RetrofitService.create()
        val ratingUsersList = mutableListOf<GetRatingUserResponse.GetRatingUserResultUser>()
        var ratingUserData = mutableListOf<PostRatingUserRequest>()
        var partyid: Long = 1

        retro.GetRatingUser(1, partyid)
            .enqueue(object : Callback<GetRatingUserResponse> {
                override fun onResponse(
                    call: Call<GetRatingUserResponse>,
                    response: Response<GetRatingUserResponse>
                ) {
                    if (response.isSuccessful) {
                        val ratingusers = response.body()?.result?.users
                        Log.d("bap", "onResponse 성공 ")
                        // handle successful response
                        ratingusers?.let { ratingUsersList.addAll(it) }
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        // handle error response
                    }
                }

                override fun onFailure(call: Call<GetRatingUserResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })
        //통신과정

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RatingUserAdapter(ratingUsersList)
    }
}