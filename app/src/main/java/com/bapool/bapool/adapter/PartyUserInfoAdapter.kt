package com.bapool.bapool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.ChattingAndPartyUserItemBinding
import com.bapool.bapool.retrofit.data.FirebaseUserInfo

class PartyUserInfoAdapter(
    val context: Context,
    val groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>>,
    val groupOnerId: String,
) : RecyclerView.Adapter<PartyUserInfoAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ChattingAndPartyUserItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(groupUserInfo[position])

    }

    override fun getItemCount(): Int {
        return groupUserInfo.size
    }


    inner class ViewHolder(private val binding: ChattingAndPartyUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindItems(item: Map<String, FirebaseUserInfo>) {


            val userInfo = item.values.firstOrNull()

            if (userInfo != null) {
                binding.userId.text = userInfo.nickName
                //어플리케이션 내에서 받아오는중
                val imageName = "image${userInfo.imgUrl}"
                val resourceId =
                    context.resources.getIdentifier(imageName, "drawable", context.packageName)
                binding.userImage.setImageResource(resourceId)


//                //firebase storage에서 이미지 받아오기
//                val storageReference = Firebase.storage.reference.child("image"+ "${userInfo.imageUrl}"+".png")
//
//                val imageView = binding.userImage
//
//                storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        Glide.with(context)
//                            .load(task.result)
//                            .into(imageView)
//                    } else {
//                        Log.d("ChattingAdapter","image"+ "${userInfo.imageUrl}" + ".png")
//                    }
//                })

            }


        }
    }


}