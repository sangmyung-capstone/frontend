package com.bapool.bapool.ui

import android.content.Intent
import android.graphics.Bitmap
import com.bapool.bapool.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.PartyChattingAdapter
import com.bapool.bapool.adapter.PartyUserInfoAdapter
import com.bapool.bapool.databinding.ActivityChattingAndPartyInfoMfactivityBinding
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.retrofit.fcm.NotiModel
import com.bapool.bapool.retrofit.fcm.PushNotification
import com.bapool.bapool.retrofit.fcm.RetrofitInstance
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChattingAndPartyInfoMFActivity : AppCompatActivity() {
    //바인딩
    private lateinit var binding: ActivityChattingAndPartyInfoMfactivityBinding

    //realtime database
    private lateinit var database: DatabaseReference

    //navigation drawer toggle
    lateinit var toggle: ActionBarDrawerToggle

    //user Img nickname 에 넣을 데이터
    var groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>> = arrayListOf()
    var groupOnerId: String = ""

    //fcm에 들어갈 데이터
    var fcmUserInfo: MutableMap<String, FirebaseUserInfo> = HashMap()

    //viewModel
    private val partyInfoViewModel: PartyInfoViewModel by viewModels()

    //RecyclerView adapter 연결
    private lateinit var chattingRVA: PartyChattingAdapter
    lateinit var chattingRecyclerView: RecyclerView
    var partyUserInfo: MutableMap<String, FirebaseUserInfo> = HashMap()
    var peopleCount: Int = 0

    //임시 userId,groupId
    var currentUserId: String = "userId2"
    var partyId: String = "groupId1"
    private lateinit var currentUserNickName: String

//    //임시 userId,groupId
//    val testCurrentUserId = "userId3"
//    val testPartyId = "groupId1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingAndPartyInfoMfactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        initializeVari()
        listener()
        getPartyUserInfo()


    }


    fun initializeVari() {
        database = Firebase.database.reference
        chattingRecyclerView = binding.chattingRv
        //intent로 받아올 userId랑 partyId

//        currentUserId = intent.getStringExtra("currentUserId").toString()
//        partyId = intent.getStringExtra("partyId").toString()


        //그룹 이름 지정
        initGroupName()

        //사진, 닉네임 지정
        initImgName()


    }


    fun getPartyUserInfo() {

        database.child("Groups").child(partyId)
            .child("groupUsers").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    partyUserInfo.clear()
                    peopleCount = 0
                    for (data in snapshot.children) {
                        val userId = data.key.toString()
                        var userInfo: FirebaseUserInfo
                        peopleCount++

                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        userInfo = snapshot.getValue(FirebaseUserInfo::class.java)!!
                                        partyUserInfo[userId] = userInfo
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                    }
                                }
                            )
                    }
                    ChattingAdapter()
                    chattingRVA.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }


    fun listener() {
        binding.menuButton.setOnClickListener {

        }
        binding.sendIcon.setOnClickListener {
            sendMessage()
        }
        //사진 채팅창에 등록 버튼
        binding.sendImgBtn.setOnClickListener {
            saveImg()

        }

        toggle = ActionBarDrawerToggle(this@ChattingAndPartyInfoMFActivity,
            binding.drawerNavigationLayout,
            R.string.open,
            R.string.close)
        binding.drawerNavigationLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.menuButton.setOnClickListener {
            binding.drawerNavigationLayout.openDrawer(GravityCompat.END)
        }

        binding.menuEditPartyInfo.setOnClickListener {
            editPartyInfo()
        }
        binding.detailOnChattingBackground.setOnClickListener {

        }
        binding.detailIcon.setOnClickListener {

        }
        binding.menuPartyOut.setOnClickListener {

        }


    }


    //그룹 이름 데이터베이스에서 가져와서 넣기
    fun initGroupName() {
        //그룹 이름 가져오기
        database.child("Groups").child(partyId).child("groupInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(FirebasePartyInfo::class.java)!!
                    binding.GrpName.setText(item.groupName)
                    binding.partyNameNv.setText(item.groupName)
                    binding.partyMenuNv.setText(item.groupMenu)
                    binding.startDateTextNv.setText(item.startDate)
                    val currentMaxPeople = "${item.curNumberOfPeople} / ${item.maxNumberOfPeople}"
                    binding.currentMaxPeople.setText(currentMaxPeople)
                    partyInfoViewModel.setObjectInfo(item)
                    Log.d("sdajdfkj", partyInfoViewModel.getObjectInfo().toString())
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

    fun initImgName() {
        database.child("Groups").child(partyId).child("groupUsers").addValueEventListener(
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
                                    val userInfo =
                                        snapshot.getValue(FirebaseUserInfo::class.java)
                                    val item =
                                        mapOf(userId to userInfo)
                                    groupUserInfo.add(item as Map<String, FirebaseUserInfo>)
                                    if (!userId.equals(currentUserId)) {
                                        fcmUserInfo.put(userId, userInfo!!)
                                    } else {
                                        currentUserNickName = userInfo?.nickName ?: ""
                                    }
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
        GroupInfoAdapter(groupUserInfo, groupOnerId)
    }


    //recyclerview 어댑터
    fun GroupInfoAdapter(
        groupUserInfo: ArrayList<Map<String, FirebaseUserInfo>>,
        groupOnerId: String,
    ) {

        val userRv = binding.userRv
        val userImgNameAdapter = PartyUserInfoAdapter(this, groupUserInfo, groupOnerId)
        userRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        userRv.adapter = userImgNameAdapter

    }


    //테스트 임시 클래스
    fun getTime(): String {
        val currentTime = System.currentTimeMillis()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val date = sdf.format(currentTime)
        return date.toString()
    }

    fun ChattingAdapter() {

        chattingRVA =
            PartyChattingAdapter(chattingRecyclerView,
                this,
                currentUserId,
                partyId,
                partyUserInfo, peopleCount)
        chattingRecyclerView.adapter = chattingRVA
        chattingRecyclerView.layoutManager = LinearLayoutManager(this)

    }

    fun sendMessage() {
        val messageText = binding.sendMessage.text.toString()
        if (messageText != "") {
            val group_messages =
                FirebasePartyMessage(currentUserId, getTime(), messageText, 0)
            database.child("Groups").child(partyId).child("groupMessages").push()
                .setValue(group_messages)
                .addOnSuccessListener {
                    binding.sendMessage.text.clear()
                }
            for ((key, userInfo) in fcmUserInfo.entries) {
                sendFcm(0, userInfo)
            }
        }


    }

    private fun testPush(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {

        RetrofitInstance.api.postNotification(notification)

    }


    fun sendFcm(messageType: Int, userInfo: FirebaseUserInfo) {
        val getterToken = userInfo.token.toString()
        val msgText: String = if (messageType == 1) {
            "사진"
        } else {
            binding.sendMessage.text.toString()
        }
        val notiModel = NotiModel(currentUserNickName, msgText)

        val pushModel = PushNotification(notiModel, getterToken)

        testPush(pushModel)
    }


    fun saveImg() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100) {

            val databaseRef = database.child("Groups").child(partyId).child("groupMessages")
            val ImgRef = databaseRef.push()
            val uid = ImgRef.key

            data?.data?.let { uri ->
                val storageRef = Firebase.storage.reference.child(partyId).child("${uid}")
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos)
                val data: ByteArray = baos.toByteArray()
                val uploadTask = storageRef.putBytes(data)
                uploadTask.continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    storageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result.toString()
                        val group_messages =
                            FirebasePartyMessage(currentUserId, getTime(), "", 1, downloadUrl)
                        ImgRef.setValue(group_messages)
                        for ((key, userInfo) in fcmUserInfo.entries) {
                            sendFcm(1, userInfo)
                        }
                    }
                }


            }
        }
    }

    // Navigation Drawer가 열려있을 경우, 뒤로가기 버튼으로 닫기
    override fun onBackPressed() {
        if (binding.drawerNavigationLayout.isDrawerOpen(GravityCompat.END)) {
            binding.drawerNavigationLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }

    fun editPartyInfo() {
        val intent = Intent(this, EditPartyInfoActivity::class.java)

        startActivity(intent)
    }


}


