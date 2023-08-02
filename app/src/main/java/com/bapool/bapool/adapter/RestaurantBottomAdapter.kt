package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.retrofit.data.Restaurant
import com.bapool.bapool.ui.fragment.MapFragment
import com.bapool.bapool.ui.fragment.MapFragment.Companion.markerList
import com.bumptech.glide.Glide
import com.naver.maps.map.NaverMap

class RestaurantBottomAdapter(
    val itemList: List<Restaurant>,
    val imageList: List<String>,
    val naverMap: NaverMap
) :
    RecyclerView.Adapter<RestaurantBottomAdapter.RestaurantViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_item, parent, false)
        return RestaurantViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.restaurant_name.text = itemList[position].restaurant_name
        holder.restaurant_category.text = itemList[position].category
        holder.restaurant_address.text = itemList[position].restaurant_address
        holder.restaurant_group_number.text = itemList[position].num_of_party.toString()

        Log.d("bottom_view_holder", "view holder${position} run")
        Log.d("bottom_view_holder", "image list ${position} run")

        if (imageList[position] == null)
            Glide.with(holder.context)
//            .load(itemList[position].imgURL)
                .load(R.drawable.bapool)
                .into(holder.restaurant_img)
        else    // adapter.notifyItemChanged(position) 호출 시
            Glide.with(holder.context)
                .load(imageList[position])
                .error(R.drawable.hashtag5)
                .into(holder.restaurant_img)
        holder.restaurant_img.clipToOutline = true


        holder.itemView.setOnClickListener {
            Log.d("bottom_view_holder", "view holder${position} touch")
            Log.d(
                "bottom_view_holder",
                "id : ${itemList[position].restaurant_id}\n" +
                        "name : ${itemList[position].restaurant_name}" +
                        "image list : ${imageList[position]}"
            )
            MapFragment.getInstance()?.markerGoEvent(
                naverMap,
                markerList[position],
                itemList[position].restaurant_id,
                itemList[position].restaurant_longitude,
                itemList[position].restaurant_latitude
            )
        }
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
        val restaurant_group_number =
            itemView.findViewById<TextView>(R.id.bottom_restaurant_group_number)
    }
}