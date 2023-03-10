package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.MygrplistItemsBinding
import com.bapool.bapool.retrofit.data.MyGroupListModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyGroupListAdapter(
    val context: Context,
    private val myGroupList: ArrayList<MyGroupListModel>
) :
    RecyclerView.Adapter<MyGroupListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MygrplistItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(myGroupList[position])
    }

    override fun getItemCount(): Int {
        return myGroupList.size
    }


    inner class ViewHolder(private val binding: MygrplistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: MyGroupListModel) {

            val allNum = partiNum(item.participantNum, item.deadlineNum)
            val allDate = dateRange(item.startDate, item.endDate)
            binding.resName.text = item.resName
            binding.grpName.text = item.grpName
            binding.lastChat.text = item.lastChat
            binding.notReadChat.text = item.notReadChat.toString()
            binding.participantsNum.text = allNum
            binding.date.text = allDate

        }
    }

    //localdatetime을 범위로 만드는 작업
    fun dateRange(startDate: LocalDateTime, endDate: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("MMM d일, h:mm a")
        Log.d("datestart",startDate.format(formatter))
        Log.d("datend",endDate.format(formatter))
        val range: String =
            "${startDate.format(formatter)} - ${endDate.format(formatter)}"
        return range

    }

    //참여인원, 정원 String으로 한줄 만들기
    fun partiNum(participantsNum: Int, deadlineNum: Int): String {
        var allNum: String = "${participantsNum} / ${deadlineNum}"
        return allNum
    }
}

