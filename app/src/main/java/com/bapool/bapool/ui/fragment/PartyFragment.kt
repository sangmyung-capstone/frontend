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
import com.bapool.bapool.retrofit.fcm.FirebaseService
import com.google.android.gms.tasks.OnCompleteListener
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

    var currentUserId: String = "3"



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

//
//        binding.dummyBtn.setOnClickListener {
////
//            // 채팅 더미데이터 추가
////            val database = Firebase.database
////            val messageText = "Test2"
////            val groupMessages =
////                FirebasePartyMessage("1", getTime(), messageText)
////            database.getReference("Groups").child("1").child("groupMessages").push()
////                .setValue(groupMessages)
////
////
//////            Toast.makeText(this, "취소버튼", Toast.LENGTH_SHORT).show()
////            그룹 더미데이터 추가
////            val database = Firebase.database
////            val myRef = database.getReference("Groups")
////            val hashtaglist = mutableListOf<Int>()
////            hashtaglist.add(4)
////            hashtaglist.add(3)
////            val groupInfo = FirebasePartyInfo("그룹이름3", "메뉴3", "상세메뉴3",
////                2, 3, getTime(), getTime(), hashtaglist, "식당이름3",33 ,"www.google.com")
////            val groupUsers = mapOf("22" to true, "33" to true)
////            val Group3 = FirebaseParty(groupInfo, null, groupUsers)
////
////            myRef.child("33").setValue(Group3)
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
//        }

        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        myPartyRv = binding.myGroupListRv

    }


    //recyclerView adapter
    fun adapter(context: Context, list: List<MyPartyListModel>) {
        myPartyAdapter = MyPartyListAdapter(context, list, currentUserId.toString())
        myPartyRv.adapter = myPartyAdapter
        myPartyRv.layoutManager = LinearLayoutManager(context)
    }

    fun getUserPartyData() {
        FirebaseDatabase.getInstance().getReference("test").child("Groups")
            .orderByChild("groupUsers/$currentUserId")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
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
                            if (!(currentUserId.toString() in data.confirmed))
                                notReadChatNumber += 1
                        }

                        val grpId = data.key.toString()
                        val resName: String = ""
                        val grpName = partyInfo?.groupName ?: ""
                        val participants = partyInfo?.curNumberOfPeople ?: 0
                        val lastChat: String = if (lastChatItem.type == 1) {
                            "사진"
                        } else {
                            lastChatItem.content
                        }
                        val lastChatTime: String = lastChatItem.sendedDate

                        val dataModel = MyPartyListModel(grpId,
                            resName,
                            grpName, participants, lastChat, notReadChatNumber, lastChatTime)
                        myPartyListModel.add(dataModel)

                    }


                    Log.d("채팅방확인소트전", myPartyListModel.toString())
                    val sortedMyPartyListModel =
                        myPartyListModel.sortedByDescending { it.lastChatTime }
                    Log.d("채팅방확인소트후", sortedMyPartyListModel.toString())
                    adapter(requireContext(), sortedMyPartyListModel)
                    myPartyAdapter.notifyDataSetChanged()

                }


                override fun onCancelled(error: DatabaseError) {

                }


            })
    }

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getTime(): String {
        val currentTime = System.currentTimeMillis()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val date = sdf.format(currentTime)
        return date.toString()
    }
}
