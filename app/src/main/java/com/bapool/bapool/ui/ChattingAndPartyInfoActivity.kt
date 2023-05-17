package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class ChattingAndPartyInfoActivity : AppCompatActivity() {
    //바인딩
    private lateinit var binding: ActivityChattingAndPartyInfoBinding

    //realtime database
    private lateinit var database: DatabaseReference

    //user Img nickname 에 넣을 데이터
    var groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>> = arrayListOf()
    var groupOnerId: String = ""

    //viewModel
    private lateinit var partyInfoViewModel: PartyInfoViewModel

    //임시 userId,groupId
    val currentUserId = "userId1"
    val groupId = "groupId1"

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
        //viewModel
        partyInfoViewModel = ViewModelProvider(this).get(PartyInfoViewModel::class.java)
        //그룹 이름 지정
        initGroupName()
        //사진, 닉네임 지정
        initImgName()



    }


    fun listener() {
        binding.cancelButton.setOnClickListener {
//
            // 채팅 더미데이터 추가
//            val messageText = "Test2"
//            val groupMessages =
//                FirebasePartyMessage("userId3", getTime(), messageText)
//            database.child("Groups").child("groupId1").child("groupMessages").push()
//                .setValue(groupMessages)


//            Toast.makeText(this, "취소버튼", Toast.LENGTH_SHORT).show()
////            그룹 더미데이터 추가
//            val database = Firebase.database
//            val myRef = database.getReference("Groups")
//            val hashtaglist = mutableListOf<Int>()
//            hashtaglist.add(4)
//            hashtaglist.add(3)
//            val groupInfo = FirebasePartyInfo("그룹이름1", "메뉴1", "상세메뉴1",
//                1, 4, getTime(), getTime(), hashtaglist)
//            val groupUsers = mapOf("userId3" to false)
//            val Group3 = FirebaseParty(groupInfo, groupUsers)
//
//            myRef.child("groupId1").setValue(Group3)
//            myRef.child("groupId1").child("groupUsers").updateChildren(groupUsers)
//            Users 더미데이터 추가
//            val database = Firebase.database
//            val myRef = database.getReference("Users")
//            val banUsers = mutableListOf<String>()
//            banUsers.add("userId4")
//            banUsers.add("userId7")
//            val userInfo = FirebaseUserInfo("8", "3이에용", banUsers)
//            myRef.child("userId3").setValue(userInfo)

        }
    }

    //그룹 이름 데이터베이스에서 가져와서 넣기
    fun initGroupName() {
        //그룹 이름 가져오기
        database.child("Groups").child(groupId).child("groupInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(FirebasePartyInfo::class.java)
                    partyInfoViewModel.liveData(item)
                    binding.GrpName.setText(item.groupName)
                    val hashtagList = item.hashTag
                    val liveData = partyInfoViewModel.liveData.value
                    Log.d("initGroupName", liveData.toString())
                    binding.hashtagVisible.visibility = View.VISIBLE
                    for (item in hashtagList) {
                        when (item) {
                            1 -> binding.hash1.visibility = View.VISIBLE
                            2 -> binding.hash2.visibility = View.VISIBLE
                            3 -> binding.hash3.visibility = View.VISIBLE
                            4 -> binding.hash4.visibility = View.VISIBLE
                            5 -> binding.hash5.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

    fun initImgName() {
        database.child("Groups").child(groupId).child("groupUsers").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    groupUserInfo.clear()
                    val expectedCount = snapshot.childrenCount
                    var completedCount = 0

                    for (data in snapshot.children) {
                        if (data.value == true) {
                            groupOnerId = data.key.toString()
                        }

                        database.child("Users").child("${data.key}")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userId = snapshot.key.toString()
                                    val userInfo = snapshot.getValue(FirebaseUserInfo::class.java)
                                    val item =
                                        mapOf(userId to userInfo)
                                    groupUserInfo.add(item as Map<String, FirebaseUserInfo>)
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
        adapter(groupUserInfo, groupOnerId)
    }


    fun addGroupUserInfo(groupUserInfoOne: Map<String, FirebaseUserInfo>) {
        groupUserInfo.add(groupUserInfoOne)
    }

    //recyclerview 어댑터
    fun adapter(groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>>, groupOnerId: String) {
        val userRv = binding.userRv
        val userImgNameAdapter = PartyUserInfoAdapter(this, groupUserInfo, groupOnerId)
        userRv.adapter = userImgNameAdapter
        userRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }


    //테스트 임시 클래스
    fun getTime(): String {
        val currentTime = System.currentTimeMillis()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val date = sdf.format(currentTime)
        return date.toString()
    }


}

class PartyInfoViewModel : ViewModel() {
    private val _liveData = MutableLiveData<FirebasePartyInfo>()
    val liveData: LiveData<FirebasePartyInfo> get() = _liveData

    fun loadData(item: FirebasePartyInfo){
        _liveData.value = item
    }
    fun getData() : MutableLiveData<FirebasePartyInfo> {
        return _liveData
    }

}
