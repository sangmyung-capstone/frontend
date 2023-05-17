package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.MypartylistItemsBinding
import com.bapool.bapool.retrofit.data.MyPartyListModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyGrpListAdapter(
    val context: Context,
    private val myGroupList: ArrayList<MyPartyListModel>
) :
    RecyclerView.Adapter<MyGrpListAdapter.ViewHolder>() {

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
            val allNum = partiNum(item.participants, item.max_people)
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
        val formatterStart = DateTimeFormatter.ofPattern("MMM d일, H:mm")
        val formatterEnd = DateTimeFormatter.ofPattern("H:mm")
        Log.d("datestart", startDate.format(formatterStart))
        Log.d("datend", endDate.format(formatterEnd))
        val range: String =
            "${startDate.format(formatterStart)} - ${endDate.format(formatterEnd)}"
        return range

    }

    //참여인원, 정원 String으로 한줄 만들기
    fun partiNum(participantsNum: Int, deadlineNum: Int): String {
        var allNum: String = "${participantsNum} / ${deadlineNum}"
        return allNum
    }


}

