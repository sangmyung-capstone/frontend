package com.bapool.bapool.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.databinding.LoglistItemsBinding
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.RatingActivity
import com.bumptech.glide.Glide

class RestaurantLogViewHolder(val binding: LoglistItemsBinding) :
    RecyclerView.ViewHolder(binding.root)

class RestaurantLogAdapter(private val datas: MutableList<GetRestaurantLogResponse.parties>) :
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
        val context = binding.root.context
        // Set the data to the views
        binding.retaurantname.text = Loglist.restaurant_name
        binding.address.text = Loglist.restaurant_address
        binding.category.text = Loglist.category
        binding.groupname.text = Loglist.party_name

        //Glide 쓰는 과정
        if (Loglist.restaurant_img_url != null) {
            Glide.with(holder.itemView).load(Loglist.restaurant_img_url).into(binding.restaurantImage)
        } else if (Loglist.restaurant_img_url == null) {
            binding.restaurantImage.setImageResource(R.drawable.restaurant_icon)
        }

        binding.ratingbutton.isEnabled = !Loglist.rating_complete
        if(binding.ratingbutton.isEnabled == false){
            binding.ratingbutton.setText("유저평가 완료")
        }

        binding.ratingbutton.setOnClickListener {
            val intent = Intent(context, RatingActivity::class.java)
            intent.putExtra("party_id", Loglist.party_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = datas.size
}