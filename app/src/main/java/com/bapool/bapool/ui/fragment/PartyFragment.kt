package com.bapool.bapool.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.MyPartyListAdapter
import com.bapool.bapool.databinding.FragmentPartyBinding
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.text.SimpleDateFormat


class PartyFragment : Fragment() {
    private var _binding: FragmentPartyBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPartyAdapter: MyPartyListAdapter
    lateinit var myPartyRv: RecyclerView

    var myPartyListModel = arrayListOf<MyPartyListModel>()

    private lateinit var myPartyListDatabase: DatabaseReference
    lateinit var valueEventListener: ValueEventListener

    var currentUserId: String = UserId.toString()

    private val TAG = "PartyFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentPartyBinding.inflate(inflater, container, false)
        getUserPartyData()
        initializeVari()




//            // 채팅 더미데이터 추가
////            val database = Firebase.database
////            val messageText = "Test2"
////            val groupMessages =
////                FirebasePartyMessage("1", getTime(), messageText)
////            database.getReference("Groups").child("1").child("groupMessages").push()
////                .setValue(groupMessages)
////
////
////           Toast.makeText(this, "취소버튼", Toast.LENGTH_SHORT).show()
//            그룹 더미데이터 추가
//            val database = Firebase.database
//            val myRef = database.getReference("Groups")
//            val hashtaglist = mutableListOf<Int>()
//            hashtaglist.add(4)
//            hashtaglist.add(3)
//            val groupInfo = FirebasePartyInfo("그룹이름3", "메뉴3", "상세메뉴3",
//                2, 3, getTime(), getTime(), hashtaglist, "식당이름3",33 ,"www.google.com")
//            val groupUsers = mapOf("22" to true, "33" to true)
//            val Group3 = FirebaseParty(groupInfo, null, groupUsers)
//
//            myRef.child("33").setValue(Group3)
////            myRef.child("1").child("groupUsers").updateChildren(groupUsers)
////
////            Users 더미데이터 추가
////            val database = Firebase.database
////            val myRef = database.getReference("Users")
////
////            val banUsers = mutableListOf<String>()
////            banUsers.add("22")
////            banUsers.add("11")
////            val userInfo = FirebaseUserInfo("3", "3이에용", banUsers,token)
////            myRef.child("33").setValue(userInfo)
////            val userInfo2 = FirebaseUserInfo("4", "4이에용", banUsers,token)
////            myRef.child("44").setValue(userInfo2)
////            val userInfo3 = FirebaseUserInfo("5", "5이에용", banUsers,token)
////            myRef.child("55").setValue(userInfo3)
//
//
//
//            FirebaseMessaging.getInstance().token.addOnCompleteListener(
//                OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.e("dsksdfkjsfkj", "token.toString()")
//
//                        return@OnCompleteListener
//                    }
//
//                    // Get new FCM registration token
//                    val token = task.result
//
//                    // Log and toast
//                    Log.e("dsksdfkjsfkj", token.toString())
//
//                })

//            val database = Firebase.database
//            val hashtaglist = mutableListOf<Int>()
//            hashtaglist.add(1)
//            hashtaglist.add(0)
//            hashtaglist.add(1)
//            hashtaglist.add(0)
//            hashtaglist.add(1)
//            database.getReference("Groups").child("33").child("groupInfo").child("hashtTag").setValue(hashtaglist)
//            val testObject = FirebaseTest("수정 1", hashtaglist, 12)
//            database.getReference("Groups").child("33").child("groupInfo").setValue(testObject)


//        binding.dummy.setOnClickListener {
//            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val token = task.result
//                    // Use the FCM token as needed
//                    // For example, send it to your server to target specific devices
//                    // or subscribe to topics
//                    println("FCM Token: $token")
//                    Log.d("sdfasdfsafadsfasdfasdf",token)
//                } else {
//                    println("Failed to get FCM token: ${task.exception}")
//                }
//            }
//        }

        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        myPartyRv = binding.myGroupListRv

    }


    //recyclerView adapter
    fun adapter(context: Context, list: List<MyPartyListModel>) {
        myPartyAdapter = MyPartyListAdapter(context, list)
        myPartyRv.adapter = myPartyAdapter
        myPartyRv.layoutManager = LinearLayoutManager(context)
    }

    fun getUserPartyData() {
        myPartyListDatabase = FirebaseDatabase.getInstance().getReference("test").child("Groups")
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                myPartyListModel.clear()
                for (data in snapshot.children) {
                    Log.d(TAG, data.value.toString())
                    Log.d(TAG, data.key.toString())
                    val partyData = data.getValue(FirebaseParty::class.java)
                    val partyInfo = partyData?.groupInfo
                    var partyMessageMap: Map<String, FirebasePartyMessage>? =
                        partyData?.groupMessages
                    val partyMessage: Collection<FirebasePartyMessage> =
                        partyMessageMap!!.values
                    val sortedMessage = partyMessage.sortedBy { it.sendedDate }


                    val lastChatItem: FirebasePartyMessage =
                        if (sortedMessage.isNullOrEmpty()) {
                            FirebasePartyMessage()
                        } else {
                            sortedMessage[sortedMessage.lastIndex]
                        }

                    var notReadChatNumber = 0

                    for (data in sortedMessage) {
                        if (!(UserId.toString() in data.confirmed))
                            notReadChatNumber += 1
                    }

                    val grpId = data.key.toString()
                    val resName: String = partyInfo?.restaurantName ?: ""
                    val grpName = partyInfo?.groupName ?: ""
                    val participants = partyInfo?.curNumberOfPeople ?: 0
                    val restaurantImgUrl = partyInfo?.restaurantImgUrl ?: ""
                    val lastChat: String = if (lastChatItem.type == 1) {
                        "사진"
                    } else {
                        lastChatItem.content
                    }
                    val lastChatTime: String = lastChatItem.sendedDate

                    val dataModel = MyPartyListModel(
                        grpId,
                        resName,
                        grpName, participants, lastChat, notReadChatNumber, lastChatTime,restaurantImgUrl
                    )
                    myPartyListModel.add(dataModel)

                }


                val sortedMyPartyListModel =
                    myPartyListModel.sortedByDescending { it.lastChatTime }
                adapter(requireContext(), sortedMyPartyListModel)
                myPartyAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        myPartyListDatabase
            .orderByChild("groupUsers/${UserId.toString()}")
            .equalTo(true).addValueEventListener(valueEventListener)

    }

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        myPartyListDatabase.removeEventListener(valueEventListener)
        _binding = null
    }


}
