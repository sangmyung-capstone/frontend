package com.bapool.bapool.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ChattingAndPartyUserItemBinding
import com.bapool.bapool.retrofit.data.FirebaseUserInfo
import com.bapool.bapool.ui.CheckUserProfileActivity
import com.bapool.bapool.ui.LoginActivity.Companion.UserId

class PartyUserInfoAdapter(
    val context: Context,
    val partyUserInfo: ArrayList<Map<String, FirebaseUserInfo>>,
    val partyLeaderId: String,
) : RecyclerView.Adapter<PartyUserInfoAdapter.ViewHolder>() {

    init {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChattingAndPartyUserItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(partyUserInfo[position])

    }

    override fun getItemCount(): Int {
        return partyUserInfo.size
    }


    inner class ViewHolder(private val binding: ChattingAndPartyUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: Map<String, FirebaseUserInfo>) {

            Log.d("sadfasdfadsf", partyUserInfo.toString())
            Log.d("sadfasdfadsf", item.toString())

            val userInfo = item.values.firstOrNull()

            if (userInfo != null) {
                binding.userId.text = userInfo.nickName
                //어플리케이션 내에서 받아오는중
                val imageName = "image${userInfo.imgUrl}"
                val resourceId =
                    context.resources.getIdentifier(imageName, "drawable", context.packageName)
                binding.userImage.setImageResource(resourceId)

            }
            Log.d("asdfsaf", partyLeaderId)
            Log.d("asdfsaf", item.keys.firstOrNull().toString())
            if (partyLeaderId == item.keys.firstOrNull().toString()) {
                val image = binding.userImage
                image.setBackgroundResource(R.drawable.custom_img_bg)
            } else {
                val image = binding.userImage
                image.setBackgroundResource(R.drawable.custom_img_bg_pressed)
            }
            binding.userNameImageBackground.setOnClickListener {
                val intent = Intent(context, CheckUserProfileActivity::class.java)
                intent.putExtra("opponentUserId", item.keys.firstOrNull().toString())
                context.startActivity(intent)

            }


        }
    }

}
