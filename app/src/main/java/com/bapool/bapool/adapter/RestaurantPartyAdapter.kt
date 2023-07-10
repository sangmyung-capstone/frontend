package com.bapool.bapool.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.JoinpartyCustomDialogBinding
import com.bapool.bapool.databinding.RestaurantpartylistItemsBinding
import com.bapool.bapool.retrofit.data.ResPartyList
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RestaurantPartyAdapter(val context: Context) :
    RecyclerView.Adapter<RestaurantPartyAdapter.ViewHolder>() {
    var resName: String = ""
    var resGroup = listOf<ResPartyList>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RestaurantpartylistItemsBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(resGroup[position])

        holder.joinButton.setOnClickListener {
//            Toast.makeText(context, "joinbtn clicklistener", Toast.LENGTH_SHORT).show()
//            val resGroupDialog = resGroup[position]
//            val joinPartyDialog = JoinpartyCustomDialogBinding.inflate(LayoutInflater.from(context))
//
//            dialogBinding(resGroupDialog, joinPartyDialog)
//
//
//            val mBuilder = AlertDialog.Builder(context)
//                .setView(joinPartyDialog.root)
//                .setTitle(resGroup[position].party_name)
//
//            mBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return resGroup.size
    }


    inner class ViewHolder(private val binding: RestaurantpartylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val joinButton: Button = binding.joinGrp
        fun bindItem(item: ResPartyList) {

            //차단유저 보이게하기
            if (item.has_block_user) {
                binding.ban.visibility = View.VISIBLE
            }
            //hashtag 보이게하기
            val hashtagList: List<Int>? = item.hashtag
            if (hashtagList != null) {
                if (hashtagList.isNotEmpty()) {
                    binding.hashtagVisible.visibility = View.VISIBLE
                    for (item in hashtagList) {
                        when (item) {
                            1 -> binding.hash1.visibility = View.VISIBLE
                            2 -> binding.hash2.visibility = View.VISIBLE
                            3 -> binding.hash3.visibility = View.VISIBLE
                            4 -> binding.hash4.visibility = View.VISIBLE
                            5 -> binding.hash5.visibility = View.VISIBLE
                        }
                    }
                }
            }
            //만약 참여중인 그룹이라면 button을 다르게 표시
            if (item.is_participate) {
                Log.d("is_participate", (!item.is_participate).toString())
                binding.joinGrp.isEnabled = false
                binding.joinGrp.text = "참여중"
            }

            val allNum = partiNum(item.participants, item.max_people)
            val allDate = dateRange(item.start_date, item.end_date)

            binding.menu.text = item.menu
            binding.date.text = allDate
            binding.participantsNum.text = allNum
            binding.grpName.text = item.party_name
            binding.detail.text = item.detail
            binding.rating.text = item.rating?.toString()

        }

    }

    //날짜 변환 함수
    fun dateRange(startDate: String, endDate: String): String {
        val start_date = LocalDateTime.parse(startDate)
        val end_date = LocalDateTime.parse(endDate)

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

//    fun dialogBinding(item: ResPartyList, binding: JoinpartyCustomDialogBinding) {
//        //차단유저 보이게하기
//        if (item.has_block_user) {
//            binding.ban.visibility = View.VISIBLE
//        }
//        //hashtag 보이게하기
//        val hashtagList: List<Int> = item.hashtag
//        if (hashtagList.isNotEmpty()) {
//            binding.hashtagVisible.visibility = View.VISIBLE
//            for (item in hashtagList) {
//                when (item) {
//                    1 -> binding.hash1.visibility = View.VISIBLE
//                    2 -> binding.hash2.visibility = View.VISIBLE
//                    3 -> binding.hash3.visibility = View.VISIBLE
//                    4 -> binding.hash4.visibility = View.VISIBLE
//                    5 -> binding.hash5.visibility = View.VISIBLE
//                }
//            }
//        }
//
//        val allNum = partiNum(item.participants, item.max_people)
//        val allDate = dateRange(item.start_date, item.end_date)
//
//        binding.menu.text = item.menu
//        binding.date.text = allDate
//        binding.participantsNum.text = allNum
//        binding.detail.text = item.detail
//        binding.rating.text = item.rating.toString()
//        binding.gotoChattingGrp.setOnClickListener {
//
//        }
//
//    }
}

