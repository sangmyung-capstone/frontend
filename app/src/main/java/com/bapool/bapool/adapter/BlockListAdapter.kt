package com.bapool.bapool.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.BlocklistItemsBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BlockListViewHolder(val binding: BlocklistItemsBinding) :
    RecyclerView.ViewHolder(binding.root)

class BlockListAdapter(val datas: MutableList<GetBlockUserResponse.BlockedUser>) :
    RecyclerView.Adapter<BlockListViewHolder>() {

    interface BlockButtonClickListener {
        fun onBlockButtonClicked()
    }

    fun setBlockButtonClickListener(listener: BlockButtonClickListener) {
        blockButtonClickListener = listener
    }

    private var blockButtonClickListener: BlockButtonClickListener? = null


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

        val formatter = DateTimeFormatter.ISO_INSTANT
        val dateTime = LocalDateTime.parse(blockedUser.block_date, formatter)

        // Set the data to the views
        var blockdate = blockedUser.block_date.toString()
        binding.Blocnickname.text = blockedUser.nickname
        binding.Blockdate.text = "${dateTime.year}-${dateTime.monthValue}-${dateTime.dayOfMonth}"

        binding.blockbutton.setOnClickListener {
            // Handle the button click event
            val retro = ServerRetrofit.create()
            var userInfo = BlockUserRequest(blockedUser.user_id)

            retro.PostBlockUser(UserId!!, userInfo)
                .enqueue(object : Callback<BlockUserResponse> {
                    override fun onResponse(
                        call: Call<BlockUserResponse>,
                        response: Response<BlockUserResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Log.d("bap", "onResponse 성공 $result")
                            blockButtonClickListener?.onBlockButtonClicked()
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