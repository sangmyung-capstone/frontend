package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.ResgrplistItemsBinding
import com.bapool.bapool.retrofit.data.ResGroupList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ResGrpListAdapter(val context: Context) :
    RecyclerView.Adapter<ResGrpListAdapter.ViewHolder>() {
    var resName: String = ""
    var resGroup = listOf<ResGroupList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ResgrplistItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.bindItem(resGroup[position])
    }

    override fun getItemCount(): Int {
        return resGroup.size
    }


    inner class ViewHolder(private val binding: ResgrplistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItem(item: ResGroupList) {
            //차단유저 보이게하기
            if (item.has_block_user) {
                binding.ban.visibility = View.VISIBLE
            }
            //hashtag 보이게하기
            val hashtagList: ArrayList<String> = item.hashtag
            for (item in hashtagList) {
                when (item) {
                    "1" -> binding.hash1.visibility = View.VISIBLE
                    "2" -> binding.hash2.visibility = View.VISIBLE
                    "3" -> binding.hash3.visibility = View.VISIBLE
                    "4" -> binding.hash4.visibility = View.VISIBLE
                    "5" -> binding.hash5.visibility = View.VISIBLE
                }
            }


            val allNum = partiNum(item.participants, item.max_people)
            val allDate = dateRange(item.start_date, item.end_date)


            binding.date.text = allDate
            binding.participantsNum.text = allNum
            binding.grpName.text = item.menu
            binding.detail.text = item.detail
            binding.rating.text = item.rating.toString()

        }

    }

    //날짜 변환 함수
    fun dateRange(startDate: String, endDate: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val start_date = LocalDateTime.parse(startDate, formatter)
        val end_date = LocalDateTime.parse(startDate, formatter)


        val formatterStart = DateTimeFormatter.ofPattern("MMM d일, H:mm")
        val formatterEnd = DateTimeFormatter.ofPattern("H:mm")
        Log.d("datestart", startDate.format(formatterStart))
        Log.d("datend", endDate.format(formatterEnd))
        val range: String =
            "${start_date.format(formatterStart)} - ${end_date.format(formatterEnd)}"
        return range

    }

    //참여인원, 정원 String으로 한줄 만들기
    fun partiNum(participants: Int, max_people: Int): String {
        var allNum: String = "${participants} / ${max_people}"
        return allNum
    }

}

