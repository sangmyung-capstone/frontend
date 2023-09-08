package com.bapool.bapool.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.MypartylistItemsBinding
import com.bapool.bapool.retrofit.data.MyPartyListModel
import com.bapool.bapool.ui.ChattingAndPartyInfoMFActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class MyPartyListAdapter(
    val context: Context,
    private val myPartyList: List<MyPartyListModel>,
) :
    RecyclerView.Adapter<MyPartyListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            MypartylistItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindItem(myPartyList[position])

        holder.goToChattingBtn.setOnClickListener{
            val intent =
                Intent(context, ChattingAndPartyInfoMFActivity::class.java)
            intent.putExtra("whereAreYouFrom","party")
            intent.putExtra("partyId", myPartyList[position].grpId.toString())

            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return myPartyList.size
    }


    inner class ViewHolder(private val binding: MypartylistItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val goToChattingBtn = binding.myPartyRoom
        fun bindItem(item: MyPartyListModel) {

            binding.resName.text = item.resName
            binding.grpName.text = item.grpName
            binding.lastChat.text = item.lastChat
            if(item.notReadChat== 0){
                binding.notReadChat.visibility = View.GONE
            }else{
                binding.notReadChat.visibility = View.VISIBLE
                binding.notReadChat.text = item.notReadChat.toString()

            }
            binding.participantsNum.text = "${item.participants}"
            getImgData(binding.partyRestaurantImage, item.restaurantImgUrl)
        }
    }

    fun getImgData(partyRestaurantImage: ImageView, restaurantImgUrl: String) {


//// RequestOptions to customize Glide's behavior
        val requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache the image
            .transform(RoundedCorners(50))//
        //          .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
//            .error(R.drawable.error_image) // Image to display on error

// Load the image using Glide
        Glide.with(context)
            .load(restaurantImgUrl)
            .centerCrop()
            .apply(requestOptions)
            .into(partyRestaurantImage)
    }


}

