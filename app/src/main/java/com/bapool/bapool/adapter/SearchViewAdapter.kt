package com.bapool.bapool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R

class SearchViewAdapter() : RecyclerView.Adapter<SearchViewAdapter.SearchViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item, parent, false)
        return SearchViewViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return 45
    }

    inner class SearchViewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}