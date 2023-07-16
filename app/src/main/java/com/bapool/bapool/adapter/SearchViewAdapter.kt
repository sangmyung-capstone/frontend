package com.bapool.bapool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.retrofit.data.RestaurantSearch

class SearchViewAdapter(val itemList: List<RestaurantSearch>) : RecyclerView.Adapter<SearchViewAdapter.SearchViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return SearchViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewViewHolder, position: Int) {
        holder.restaurant_name.text = itemList[position].restaurant_name
        holder.restaurant_address.text = itemList[position].restaurant_address
        holder.restaurant_category.text = itemList[position].category
        holder.restaurant_group_number.text = itemList[position].num_of_party.toString()

    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    inner class SearchViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val restaurant_name = itemView.findViewById<TextView>(R.id.search_name)
        val restaurant_address = itemView.findViewById<TextView>(R.id.search_address)
        val restaurant_category = itemView.findViewById<TextView>(R.id.search_category)
        val restaurant_group_number = itemView.findViewById<TextView>(R.id.search_group_number)
    }
}