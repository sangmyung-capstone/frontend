package com.bapool.bapool.adapter

import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bapool.bapool.retrofit.data.FirebasePartyMessage
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.bapool.bapool.databinding.ChatitemMyBinding
import com.bapool.bapool.databinding.ChatitemMyimgBinding
import com.bapool.bapool.databinding.ChatitemOpponentimgBinding
import com.bapool.bapool.databinding.ChatitemOpponetBinding
import com.bapool.bapool.retrofit.data.FirebaseUserInfo
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.bapool.bapool.R
import com.google.firebase.storage.FirebaseStorage
import kotlin.coroutines.coroutineContext

class PartyChattingAdapter(
    private val recyclerView: RecyclerView,
    val context: Context,
    val currentUserId: String,
    val groupId: String,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messages: ArrayList<FirebasePartyMessage> = arrayListOf()
    var messageKey: ArrayList<String> = arrayListOf()
    var nickNames: ArrayList<String> = arrayListOf()
    var resourceIds: ArrayList<String> = arrayListOf()

    var databaseReference: DatabaseReference
    var valueEventListener: ValueEventListener?

    var peopleCount = 0
    val testUserId = "userId3"
    private var currentPage = 0
    private val itemsPerPage = 100


    init {
        recyclerView.postDelayed({
            recyclerView.layoutManager?.scrollToPosition(messages.size - 1)
        }, 1500)
        //diffUtil Version
        databaseReference = FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
            .child("groupMessages")
        val query = databaseReference
        valueEventListener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val oldMessages = ArrayList(messages)
                val oldMessageKeys = ArrayList(messageKey)
                messages.clear()
                messageKey.clear()
                val readUsers: MutableMap<String, FirebasePartyMessage> = HashMap()
                for (data in dataSnapshot.children) {
                    val messageObject: FirebasePartyMessage =
                        data.getValue<FirebasePartyMessage>()!!
                    val messageObject_modify: FirebasePartyMessage =
                        data.getValue<FirebasePartyMessage>()!!
                    val messageKeyObject = data.key.toString()


                    messageKey.add(messageKeyObject)
                    messageObject_modify.confirmed.put(currentUserId, true)
                    readUsers.put(messageKeyObject, messageObject_modify)
                    messages.add(messageObject)
                }
                val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return oldMessages.size
                    }

                    override fun getNewListSize(): Int {
                        return messages.size
                    }

                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int,
                    ): Boolean {
                        return oldMessageKeys[oldItemPosition] == messageKey[newItemPosition]
                    }

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int,
                    ): Boolean {
                        return oldMessages[oldItemPosition] == messages[newItemPosition]
                    }

                })
                if (messages.size > 0) {
                    if (!messages[messages.size - 1].confirmed.containsKey(currentUserId)) {
                        FirebaseDatabase.getInstance().getReference("Groups")
                            .child(groupId).child("groupMessages")
                            .updateChildren(readUsers as Map<String, FirebasePartyMessage>)
                            .addOnCompleteListener {
                            }
                    } else {
                    }
                }
                diffResult.dispatchUpdatesTo(this@PartyChattingAdapter)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("recyclerviewScroll",1.toString())

        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            1 -> {            //메시지가 내 메시지인 경우
                val binding = ChatitemMyBinding.inflate(inflater, parent, false)

                return MyMessageViewHolder(binding)
            }
            2 -> {
                val binding = ChatitemMyimgBinding.inflate(inflater, parent, false)

                return MyImgViewHolder(binding)

            }
            3 -> {
                val binding = ChatitemOpponetBinding.inflate(inflater, parent, false)

                return OtherMessageViewHolder(binding)

            }
            else -> {      //메시지가 상대 메시지인 경우
                val binding =
                    ChatitemOpponentimgBinding.inflate(inflater, parent, false)

                return OtherImgViewHolder(binding)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {               //메시지의 id에 따라 내 메시지/상대 메시지

        if (messages[position].senderId.equals(currentUserId)) {       //레이아웃 항목 초기화
            if (messages[position].type == 0) {
                return 1
            } else {
                return 2
            }
        } else {
            if (messages[position].type == 0) {
                return 3
            } else {
                return 4
            }
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (holder.itemViewType) {
            1 -> (holder as MyMessageViewHolder).bind(messages[position])
            2 -> (holder as MyImgViewHolder).bind(messages[position], position)
            3 -> (holder as OtherMessageViewHolder).bind(messages[position], position)
            else -> (holder as OtherImgViewHolder).bind(messages[position], position)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    //내 메시지 viewholder
    inner class MyMessageViewHolder(private val binding: ChatitemMyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FirebasePartyMessage) {

            binding.myContent.text = item.content
            binding.myTime.text = changeTimeFormat(item.sendedDate)
            readCount(item, binding.myConfirmed)
        }
    }

    //내 이미지 viewholder
    inner class MyImgViewHolder(private val binding: ChatitemMyimgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FirebasePartyMessage, position: Int) {
            getImageData(messageKey[position], binding.myImg)
            binding.myTime.text = changeTimeFormat(item.sendedDate)
            readCount(item, binding.myConfirmed)

            binding.myImg.setOnClickListener {
                makeDialog(messageKey[position], item.downloadUrl)

            }
        }
    }

    //상대 메시지 viewholder
    inner class OtherMessageViewHolder(private val binding: ChatitemOpponetBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FirebasePartyMessage, position: Int) {
            binding.opponentContent.text = item.content
            binding.opponentTime.text = changeTimeFormat(item.sendedDate)
            readCount(item, binding.opponentConfirmed)
            setNickNameImg(position, binding.opponentId, binding.opponentImage)
        }
    }

    //상대 이미지 viewholder
    inner class OtherImgViewHolder(private val binding: ChatitemOpponentimgBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FirebasePartyMessage, position: Int) {
            getImageData(messageKey[position], binding.opponentChatImg)
            binding.opponentTime.text = changeTimeFormat(item.sendedDate)
            readCount(item, binding.opponentConfirmed)
            setNickNameImg(position, binding.opponentId, binding.opponentImage)
            binding.opponentChatImg.setOnClickListener {
                makeDialog(messageKey[position], item.downloadUrl)
            }
        }
    }

    //읽은 사람 숫자
    fun readCount(items: FirebasePartyMessage, readcount_text: TextView) {
        if (peopleCount == 0) {
            FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                .child("groupUsers")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var users: Map<String, Boolean> = HashMap()
                        users = snapshot.getValue() as Map<String, Boolean>
                        peopleCount = users.size
                        var count: Int = peopleCount - items.confirmed.size

                        if (count > 0) {
                            readcount_text.setText(count.toString())
                            readcount_text.visibility = View.VISIBLE
                        } else {
                            readcount_text.visibility = View.GONE
                        }
                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })
        } else {
            var count: Int = peopleCount - items.confirmed.size

            if (count > 0) {
                readcount_text.setText(count.toString())
                readcount_text.visibility = View.VISIBLE
            } else {
                readcount_text.visibility = View.GONE
            }

        }


    }

    override fun getItemId(position: Int): Long {
        // Return a unique identifier for the item at the given position
        return messages[position].hashCode().toLong()
    }
    //이미지, 닉네임 배치
    fun setNickNameImg(position: Int, opponentId: TextView, opponentImage: ImageView) {
        var opponentUserId = messages[position].senderId
        FirebaseDatabase.getInstance().getReference("Users").child(opponentUserId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(FirebaseUserInfo::class.java)
                    opponentId.text = user?.nickName ?: ""
                    val imageName = "image${user?.imgUrl}"
                    val resourceId =
                        context.resources.getIdentifier(imageName,
                            "drawable",
                            context.packageName)
                    opponentImage.setImageResource(resourceId)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    //시간 양식 변형
    fun changeTimeFormat(inputTime: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.KOREA)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.KOREA)

        val date = Date()
        date.time = inputFormat.parse(inputTime)?.time ?: 0
        val formattedTime = outputFormat.format(date)

        return formattedTime
    }


    fun removeValueEventListener() {
        databaseReference.removeEventListener(valueEventListener!!)
    }

    //이미지 데이터 불러오기
    fun getImageData(ImgKey: String, Img: ImageView) {
        val storageReference = Firebase.storage.reference.child(ImgKey)
        val imageView = Img

        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .override(700, 700)
                    .into(imageView)
//                recyclerView.scrollToPosition(messages.size-1)
            } else {
                Log.d("sdfsdfkey", "실패")

            }
        })
    }

    //dialog에 띄우는 이미지, 더 크게
    fun getImageDataDialog(ImgKey: String, Img: ImageView) {
        val storageReference = Firebase.storage.reference.child(ImgKey)
        val imageView = Img
        storageReference.downloadUrl.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context)
                    .load(task.result)
                    .override(950, 1500)
                    .into(imageView)


            } else {
                Log.d("sdfsdfkey", "실패")

            }
        })

    }

    //url 로 이미지 다운로드
    fun downloadImgFromUrl(downloadUrl: String, downloadUrl1: ImageView) {

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("Image Download")
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "image.jpg")

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    //이미지 누르면 다이얼로그 띄우기
    fun makeDialog(messageKey: String, downloadUrl: String) {
        val inflater = LayoutInflater.from(context)
        val builder = AlertDialog.Builder(context)
        val customLayout = inflater.inflate(R.layout.chatting_imageview, null)
        builder.setView(customLayout)
        val imageView = customLayout.findViewById<ImageView>(R.id.chattingImgBig)
        getImageDataDialog(messageKey, imageView)
        val downloadUrlId = customLayout.findViewById<ImageView>(R.id.downloadImgBtn)
        downloadUrlId.setOnClickListener {
            downloadImgFromUrl(downloadUrl, downloadUrlId)
        }
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()
    }

    //아이템 맨 밑으로 이동

}



