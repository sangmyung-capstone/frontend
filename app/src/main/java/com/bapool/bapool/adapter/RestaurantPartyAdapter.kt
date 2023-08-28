package com.bapool.bapool.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.databinding.JoinpartyCustomDialogBinding
import com.bapool.bapool.databinding.RestaurantpartylistItemsBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.PatchEditPartyInfoResponse
import com.bapool.bapool.retrofit.data.ResPartyList
import com.bapool.bapool.retrofit.data.participateParty
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RestaurantPartyAdapter(val context: Context) :
    RecyclerView.Adapter<RestaurantPartyAdapter.ViewHolder>() {
    var resName: String = ""
    var resGroup = listOf<ResPartyList>()
    val retro = ServerRetrofit.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RestaurantpartylistItemsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return ViewHolder(binding)

    }

    interface ItemClick {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (itemClick != null) {
            holder.joinButton.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }
        holder.bindItem(resGroup[position])


    }

    override fun getItemCount(): Int {
        return resGroup.size
    }


    inner class ViewHolder(private val binding: RestaurantpartylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {


        val joinButton: Button = binding.joinGrp
        fun bindItem(item: ResPartyList) {
            binding.hash1.visibility = View.GONE
            binding.hash2.visibility = View.GONE
            binding.hash3.visibility = View.GONE
            binding.hash4.visibility = View.GONE
            binding.hash5.visibility = View.GONE
            binding.ban.visibility = View.GONE

            binding.joinGrp.text = "참여"
            binding.joinGrp.isEnabled = true
            binding.joinGrp.setTextColor(context.resources.getColor(R.color.main))
            binding.joinGrp.setBackgroundColor(context.resources.getColor(R.color.white))

            binding.menu.text = ""
            binding.date.text = ""
            binding.participantsNum.text = ""
            binding.grpName.text = ""
            binding.detail.text = ""
            binding.rating.text = ""

            //차단유저 보이게하기
            if (item.has_block_user) {
                binding.ban.visibility = View.VISIBLE
            }
            //hashtag 보이게하기
            val hashtagList: List<Int>? = item.party_hashtag
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

            //만약 참여중인 파티이라면 button을 다르게 표시
            if (item.is_participate) {
                binding.joinGrp.isEnabled = false
                binding.joinGrp.text = "참여중"
                binding.joinGrp.setBackgroundColor(context.resources.getColor(R.color.gray))
                binding.joinGrp.setTextColor(context.resources.getColor(R.color.black))

            }
            //마감된파티
            if (!item.is_recruiting) {
                binding.joinGrp.isEnabled = false
                binding.joinGrp.text = "마감"
                binding.joinGrp.setBackgroundColor(context.resources.getColor(R.color.gray))
                binding.joinGrp.setTextColor(context.resources.getColor(R.color.black))
            }

            val allNum = partiNum(item.participants, item.max_people)
            val allDate = dateRange(item.start_date)

            val value = item.user_rating.toDouble()
            val formattedValue = String.format("%.1f", value)
            binding.menu.text = item.menu
            binding.date.text = allDate
            binding.participantsNum.text = allNum
            binding.grpName.text = item.party_name
            binding.detail.text = item.detail
            binding.rating.text = formattedValue


        }


    }

    //날짜 변환 함수
    fun dateRange(startDate: String): String {
        val start_date = LocalDateTime.parse(startDate)

        val formatterStart = DateTimeFormatter.ofPattern("MMM d일 H시 mm분")
        val range: String =
            "${start_date.format(formatterStart)}"
        return range

    }


    //참여인원, 정원 String으로 한줄 만들기
    fun partiNum(participants: Int, max_people: Int): String {
        var allNum: String = "${participants} / ${max_people}"
        return allNum
    }

}

