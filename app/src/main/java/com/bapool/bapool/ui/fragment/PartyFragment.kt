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
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.google.firebase.database.*


class PartyFragment : Fragment() {
    private var _binding: FragmentPartyBinding? = null
    private val binding get() = _binding!!

    private lateinit var myPartyAdapter: MyPartyListAdapter
    lateinit var myPartyRv: RecyclerView

    var myPartyListModel = arrayListOf<MyPartyListModel>()

    private lateinit var myPartyListDatabase: DatabaseReference
    lateinit var valueEventListener: ValueEventListener


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
                    val partyMessageMap: Map<String, FirebasePartyMessage>? =
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
                        if (!(UserId.toString() in data.confirmed)){
                            notReadChatNumber += 1
                        }
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
