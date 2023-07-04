package com.bapool.bapool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.retrofit.data.Restaurant
import com.bumptech.glide.Glide

class RestaurantBottomAdapter(val itemList: List<Restaurant>) :
    RecyclerView.Adapter<RestaurantBottomAdapter.RestaurantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.restaurant_recycler_view, parent, false)
        return RestaurantViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.restaurant_name.text = itemList[position].restaurant_name
        holder.restaurant_category.text = itemList[position].category
        holder.restaurant_address.text = itemList[position].restaurant_address
        holder.restaurant_group_number.text = itemList[position].num_of_party.toString()

        Glide.with(holder.context)
//            .load(itemList[position].imgURL)
            .load(R.drawable.bapool)
            .into(holder.restaurant_img)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class RestaurantViewHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val restaurant_name = itemView.findViewById<TextView>(R.id.bottom_restaurant_name)
        val restaurant_img = itemView.findViewById<ImageView>(R.id.bottom_restaurant_image)
        val restaurant_category = itemView.findViewById<TextView>(R.id.bottom_restaurant_category)
        val restaurant_address = itemView.findViewById<TextView>(R.id.bottom_restaurant_address)
        val restaurant_group_number = itemView.findViewById<TextView>(R.id.bottom_restaurant_group_number)
    }
}