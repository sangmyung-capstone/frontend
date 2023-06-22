package com.bapool.bapool.ui.fragment

import android.content.Intent
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
import com.bapool.bapool.ui.RestaurantPartyActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

import java.text.SimpleDateFormat


class PartyFragment : Fragment() {
    private var _binding: FragmentPartyBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPartyAdapter: MyPartyListAdapter
    lateinit var myPartyRv: RecyclerView

    var myPartyListModel = arrayListOf<MyPartyListModel>()

    var currentUserId = "userId1"

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

        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        myPartyRv = binding.myGroupListRv

    }


    //recyclerView adapter
    fun adapter(list: List<MyPartyListModel>) {
        myPartyAdapter = MyPartyListAdapter(requireContext(), list, currentUserId)
        myPartyRv.adapter = myPartyAdapter
        myPartyRv.layoutManager = LinearLayoutManager(requireContext())
    }

    fun getUserPartyData() {
        FirebaseDatabase.getInstance().getReference("Groups")
            .orderByChild("groupUsers/$currentUserId")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    myPartyListModel.clear()
                    for (data in snapshot.children) {

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
//                        val lastChatItem: FirebasePartyMessage =
//                            sortedMessage[sortedMessage.lastIndex]
                        var notReadChatNumber = 0

                        Log.d("채팅방확인", partyInfo.toString())
                        Log.d("채팅방확인", partyMessageMap.toString())

                        Log.d("채팅방확인", sortedMessage.toString())


                        for (data in sortedMessage) {
                            if (!(currentUserId in data.confirmed))
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
                    adapter(sortedMyPartyListModel)
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
