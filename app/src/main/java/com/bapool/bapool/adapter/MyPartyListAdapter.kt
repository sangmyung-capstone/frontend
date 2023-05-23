package com.bapool.bapool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.MypartylistItemsBinding
import com.bapool.bapool.retrofit.data.MyPartyListModel

class MyPartyListAdapter(
    val context: Context,
    private val myGroupList: ArrayList<MyPartyListModel>
) :
    RecyclerView.Adapter<MyPartyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MypartylistItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(myGroupList[position])

    }

    override fun getItemCount(): Int {
        return myGroupList.size
    }


    inner class ViewHolder(private val binding: MypartylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(item: MyPartyListModel) {

            binding.resName.text = item.resName
            binding.grpName.text = item.grpName
            binding.lastChat.text = item.lastChat
            binding.notReadChat.text = item.notReadChat.toString()
            binding.participantsNum.text = "(${item.participants})"
        }
    }



}

