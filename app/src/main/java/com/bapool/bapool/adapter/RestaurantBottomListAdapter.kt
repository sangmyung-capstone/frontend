package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.databinding.RestaurantpartylistItemsBinding
import com.bapool.bapool.retrofit.data.Restaurant
import com.bapool.bapool.ui.fragment.MapFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.naver.maps.map.NaverMap

class RestaurantBottomListAdapter(
    val itemList: List<Restaurant>,
    val imageList: List<String>,
    val naverMap: NaverMap
) :
    ListAdapter<String, RestaurantBottomListAdapter.RestaurantBottomListViewHolder>(diffUtil) {

    inner class RestaurantBottomListViewHolder(itemView: View, val context: Context) :
        RecyclerView.ViewHolder(itemView) {
        val restaurant_name = itemView.findViewById<TextView>(R.id.bottom_restaurant_name)
        val restaurant_img = itemView.findViewById<ImageView>(R.id.bottom_restaurant_image)
        val restaurant_category = itemView.findViewById<TextView>(R.id.bottom_restaurant_category)
        val restaurant_address = itemView.findViewById<TextView>(R.id.bottom_restaurant_address)
        val restaurant_party_image =
            itemView.findViewById<ImageView>(R.id.bottom_restaurant_party)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantBottomListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_item, parent, false)
        return RestaurantBottomListViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: RestaurantBottomListViewHolder, position: Int) {
        if (itemList[position].restaurant_name.length > 11)
            holder.restaurant_name.text = itemList[position].restaurant_name.substring(0 until 11) + "..."
        else
            holder.restaurant_name.text = itemList[position].restaurant_name
        holder.restaurant_category.text = itemList[position].category.split(" > ").last()
        holder.restaurant_address.text = itemList[position].restaurant_address
        if (itemList[position].num_of_party > 0)
            Glide.with(holder.context)
                .load(R.drawable.party_icon)
                .into(holder.restaurant_party_image)


        Log.d("bottom_view_holder", "view holder${position} run")
        Log.d("bottom_view_holder", "image list ${position} run")


        Glide.with(holder.context)
            .load(R.raw.bapool_img_loading)
            .into(holder.restaurant_img)

        if (imageList[position] != "a") {
            Log.d("GLIDE", "position: $position image change")
            Glide.with(holder.context)
                .load(imageList[position])
                .error(R.drawable.bapool)
                .into(holder.restaurant_img)
        }

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
                MapFragment.markerList[position],
                itemList[position].restaurant_id,
                itemList[position].restaurant_longitude,
                itemList[position].restaurant_latitude
            )
        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

}