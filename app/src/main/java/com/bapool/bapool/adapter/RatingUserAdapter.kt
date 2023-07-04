package com.bapool.bapool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.RatinguserItemBinding
import com.bapool.bapool.retrofit.data.*

class RatingViewHolder(val binding: RatinguserItemBinding) :
    RecyclerView.ViewHolder(binding.root)

class RatingUserAdapter(private val datas: MutableList<GetRatingUserResponse.GetRatingUserResultUser>) :
    RecyclerView.Adapter<RatingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = RatinguserItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {

        val Loglist = datas[position]
        val binding = holder.binding
        // Set the data to the views
        //Glide 쓰는 과정
        binding.nickname.text = Loglist.nickname
    }

    override fun getItemCount(): Int = datas.size
}