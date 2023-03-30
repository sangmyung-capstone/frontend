package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.BlockListAdapter
import com.bapool.bapool.databinding.ActivityBlockListBinding
import com.bapool.bapool.retrofit.data.GetBlockUserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityBlockListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val retro = RetrofitService.create()
        val blockedUsersList = mutableListOf<GetBlockUserResponse.BlockedUser>()

        retro.GetBlockUser("accessToken", 1)
            .enqueue(object : Callback<GetBlockUserResponse> {
                override fun onResponse(
                    call: Call<GetBlockUserResponse>,
                    response: Response<GetBlockUserResponse>
                ) {
                    if (response.isSuccessful) {
                        val blockedUsers = response.body()?.result?.users
                        Log.d("bap", "onResponse 성공 ")
                        // handle successful response
                        blockedUsers?.let { blockedUsersList.addAll(it) }
                        binding.recyclerView.adapter?.notifyDataSetChanged()
                    } else {
                        // handle error response
                    }
                }

                override fun onFailure(call: Call<GetBlockUserResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })
        //통신과정\

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = BlockListAdapter(blockedUsersList)
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
    }
}