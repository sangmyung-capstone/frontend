package com.bapool.bapool.adapter


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.databinding.MypartylistItemsBinding
import com.bapool.bapool.retrofit.data.MyPartyListModel
import com.bapool.bapool.ui.ChattingAndPartyInfoMFActivity

class MyPartyListAdapter(
    val context: Context,
    private val myPartyList: List<MyPartyListModel>,
    val currentUserId: String
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
            intent.putExtra("currentUserId", currentUserId.toString())
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
            if(item.notReadChat==0){
                binding.notReadChat.text = ""
            }else{
                binding.notReadChat.text = item.notReadChat.toString()

            }
            binding.participantsNum.text = "(${item.participants})"
        }
    }


}

