package com.bapool.bapool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.LoglistItemsBinding
import com.bapool.bapool.retrofit.data.*
import com.bumptech.glide.Glide

class RestaurantLogViewHolder(val binding: LoglistItemsBinding) :
    RecyclerView.ViewHolder(binding.root)

class RestaurantLogAdapter(private val datas: MutableList<GetRestaurantLogResponse.Party>) :
    RecyclerView.Adapter<RestaurantLogViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantLogViewHolder {
        val binding = LoglistItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RestaurantLogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantLogViewHolder, position: Int) {
        val Loglist = datas[position]
        val binding = holder.binding
        // Set the data to the views
        binding.address.text = Loglist.restaurant_address
        binding.category.text = Loglist.category
        binding.groupname.text = Loglist.restaurant_name

        //Glide 쓰는 과정
        Glide.with(holder.itemView).load(Loglist.imgUrl).into(binding.imageView2)
    }

    override fun getItemCount(): Int = datas.size
}