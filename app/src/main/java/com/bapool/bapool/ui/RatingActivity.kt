package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.BlockListAdapter
import com.bapool.bapool.adapter.RatingUserAdapter
import com.bapool.bapool.databinding.ActivityBlockListBinding
import com.bapool.bapool.databinding.ActivityLoginBinding
import com.bapool.bapool.databinding.ActivityRatinguserBinding
import com.bapool.bapool.retrofit.data.GetBlockUserResponse
import com.bapool.bapool.retrofit.data.GetRatingUserResponse
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var _binding: ActivityRatinguserBinding? = null
private val binding get() = _binding!!

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRatinguserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retro = RetrofitService.create()
        val ratingUsersList = mutableListOf<GetRatingUserResponse.GetRatingUserResultUser>()
        var partyid: Long = 1

        retro.GetRatingUser(UserId!!, partyid)
            .enqueue(object : Callback<GetRatingUserResponse> {
                override fun onResponse(
                    call: Call<GetRatingUserResponse>,
                    response: Response<GetRatingUserResponse>
                ) {
                    if (response.isSuccessful) {
                        val ratingusers = response.body()?.result?.User
                        Log.d("bap", "onResponse 성공 ")
                        // handle successful response
                        ratingusers?.let { ratingUsersList.add(it) }
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        // handle error response
                    }
                }

                override fun onFailure(call: Call<GetRatingUserResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })
        //통신과정\

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = RatingUserAdapter(ratingUsersList)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }