package com.bapool.bapool.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.BlocklistItemsBinding
import com.bapool.bapool.retrofit.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockListViewHolder(val binding: BlocklistItemsBinding) :
    RecyclerView.ViewHolder(binding.root)

class BlockListAdapter(val datas: MutableList<GetBlockUserResponse.BlockedUser>) :
    RecyclerView.Adapter<BlockListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockListViewHolder {
        val binding = BlocklistItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BlockListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockListViewHolder, position: Int) {
        val blockedUser = datas[position]
        val binding = holder.binding

        // Set the data to the views
        binding.Blocnickname.text = blockedUser.nickname
        binding.Blockdate.text = blockedUser.block_date

        binding.blockbutton.setOnClickListener {
            // Handle the button click event
            val retro = RetrofitService.create()
            var userInfo = BlockUserRequest(15)

            retro.BlockUser(1, userInfo)
                .enqueue(object : Callback<BlockUserResponse> {
                    override fun onResponse(
                        call: Call<BlockUserResponse>,
                        response: Response<BlockUserResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("bap", "onResponse 성공 ")
                        } else {
                            // handle error response
                        }
                    }

                    override fun onFailure(call: Call<BlockUserResponse>, t: Throwable) {
                        // handle network or unexpected error
                    }
                })
            //통신과정.
        }
    }

    override fun getItemCount(): Int = datas.size
}