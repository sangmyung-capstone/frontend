package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.PartyUserInfoAdapter
import com.bapool.bapool.databinding.ActivityChattingAndPartyInfoBinding
import com.bapool.bapool.retrofit.data.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChattingAndPartyInfoActivity : AppCompatActivity() {
    //바인딩
    private lateinit var binding: ActivityChattingAndPartyInfoBinding

    //realtime database
    private lateinit var database: DatabaseReference

    //user Img nickname 에 넣을 데이터
    var groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>> = arrayListOf()
    var groupOnerUid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingAndPartyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        initializeVari()
        listener()


    }

    fun initializeVari() {
        database = Firebase.database.reference

        //그룹 이름 지정
        initGroupName()
        //사진, 닉네임 지정
        initImgName()

    }


    fun listener() {
        binding.cancelButton.setOnClickListener {
            Toast.makeText(this, "취소버튼", Toast.LENGTH_SHORT).show()
            //그룹 더미데이터 추가
//            val database = Firebase.database
//            val myRef = database.getReference("Groups")
//            val hashtaglist = mutableListOf<Int>()
//            hashtaglist.add(1)
//            hashtaglist.add(2)
//            val group_info = FirebaseGroupInfo("그룹이름1", "메뉴1", "상세메뉴1",
//                1, 4, "2023-05-30 20:55:09.939000", "2023-05-30 22:55:09.939000", hashtaglist)
//            val group_users = mapOf<String, Boolean>("333333" to false)
//            val Group1: FirebaseGroup = FirebaseGroup(group_info, null, group_users)
//
////            myRef.child("groupUid1").setValue(Group1)
//            myRef.child("groupUid1").child("group_users").updateChildren(group_users)
            //Users 더미데이터 추가
//            val database = Firebase.database
//            val myRef = database.getReference("Users")
//            val banUsers = mutableListOf<String>()
//            banUsers.add("555555")
//            val user_info = FirebaseUserInfo("3", "3이에용", banUsers)
//            myRef.child("333333").setValue(user_info)

        }
    }

    //그룹 이름 데이터베이스에서 가져와서 넣기
    fun initGroupName() {
        database.child("Groups").child("groupUid1").child("group_info").child("group_name")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.GrpName.setText(snapshot.value.toString())
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    //닉네임, 이미지 정보 데이터베이스에서 가져오기
//    fun initImgName() {
//        database.child("Groups").child("groupUid1").child("group_users").addValueEventListener(
//            object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//
//                    for (data in snapshot.children) {
//                        if (data.value == true) {
//                            groupOnerUid = data.key.toString()
//                        }
//                        database.child("Users").child("${data.key}")
//                            .addValueEventListener(object : ValueEventListener {
//                                override fun onDataChange(snapshot: DataSnapshot) {
//                                    val uid = snapshot.key.toString()
//                                    val userInfo = snapshot.getValue(FirebaseUserInfo::class.java)
//                                    val item =
//                                        mapOf(uid to userInfo) as Map<String, FirebaseUserInfo>
//                                    addGroupUserInfo(item)
//
//
//                                }
//
//                                override fun onCancelled(error: DatabaseError) {
//                                }
//                            })
//                    }
//                adapter(groupUserInfo,groupOnerUid)
//                    Log.d("ChattingParty", groupUserInfo.toString())
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            }
//        )
//
//
//    }

    fun initImgName() {
        database.child("Groups").child("groupUid1").child("group_users").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val expectedCount = snapshot.childrenCount
                    var completedCount = 0

                    for (data in snapshot.children) {
                        if (data.value == true) {
                            groupOnerUid = data.key.toString()
                        }
                        database.child("Users").child("${data.key}")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val uid = snapshot.key.toString()
                                    val userInfo = snapshot.getValue(FirebaseUserInfo::class.java)
                                    val item =
                                        mapOf(uid to userInfo) as Map<String, FirebaseUserInfo>
                                    groupUserInfo.add(item)
                                    completedCount++

                                    if (completedCount.toString() == expectedCount.toString()) {
                                        onGroupUserInfoLoaded()
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    completedCount++
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            }
        )
    }

    fun onGroupUserInfoLoaded() {
        adapter(groupUserInfo, groupOnerUid)
    }


    fun addGroupUserInfo(groupUserInfoOne: Map<String, FirebaseUserInfo>) {
        groupUserInfo.add(groupUserInfoOne)
    }

    fun adapter(groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>>, groupOnerUid: String) {
        val userRv = binding.userRv
        val userImgNameAdapter = PartyUserInfoAdapter(this, groupUserInfo, groupOnerUid)
        userRv.adapter = userImgNameAdapter
        userRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }
}