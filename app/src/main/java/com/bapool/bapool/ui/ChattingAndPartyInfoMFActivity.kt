package com.bapool.bapool.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.bapool.bapool.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.PartyChattingAdapter
import com.bapool.bapool.adapter.PartyUserInfoAdapter
import com.bapool.bapool.adapter.SelectPartyLeaderAdapter
import com.bapool.bapool.databinding.ActivityChattingAndPartyInfoMfactivityBinding
import com.bapool.bapool.databinding.JoinpartyCustomDialogBinding
import com.bapool.bapool.databinding.SelectPartyleaderDialogBinding
import com.bapool.bapool.retrofit.ServerRetrofit
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
    var partyUserInfo: MutableMap<String, FirebaseUserInfo> = HashMap()
    var partyUserInfoMenu: ArrayList<Map<String, FirebaseUserInfo>> = arrayListOf()
    var groupOnerId: String = ""

    //fcm에 들어갈 데이터
    var fcmUserInfo: MutableMap<String, FirebaseUserInfo> = HashMap()

    //retrofit
    val retro = ServerRetrofit.create()

    //Log TAG
    val TAG = "ChattingAndPartyInfoMFActivity"


    //RecyclerView adapter 연결
    private lateinit var chattingRVA: PartyChattingAdapter
    private lateinit var partyUserMenuRVA: PartyUserInfoAdapter
    lateinit var chattingRecyclerView: RecyclerView
    lateinit var partyUserMenuRecyclerView: RecyclerView

    var peopleCount: Int = 0

    //임시 userId,groupId,deleteParty, deleteUserId
    var currentUserId: String = ""
    var partyId: String = ""
    var currentPartyInfo: FirebasePartyInfo = FirebasePartyInfo()
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
        partyUserMenuRecyclerView = binding.userRv
        //intent로 받아올 userId랑 partyId

        currentUserId = intent.getStringExtra("currentUserId").toString()
        partyId = intent.getStringExtra("partyId").toString()


        //그룹 이름 지정
        initGroupName()


    }

    fun getPartyUserInfo() {

        database.child("Groups").child(partyId.toString())
            .child("groupUsers").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    partyUserInfo.clear()
                    partyUserInfoMenu.clear()
                    peopleCount = 0
                    Log.d("sadfsadfdsafsdaf", "userId.toString())")


                    for (data in snapshot.children) {
                        val userId = data.key.toString()
                        var userInfo: FirebaseUserInfo
                        peopleCount++

                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .addValueEventListener(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        userInfo = snapshot.getValue(FirebaseUserInfo::class.java)!!
                                        partyUserInfo[userId] = userInfo
                                        Log.d("sadfsadfdsafsdaf", userId.toString())
                                        Log.d("sadfsadfdsafsdaf", snapshot.key.toString())

                                        val item =
                                            mapOf(userId to userInfo)
                                        val map = getMapByUID(userId)
                                        if (map != null) {
                                            replaceMapByUID(userId, item)
                                        } else {
                                            partyUserInfoMenu.add(item)
                                        }

                                        if (!userId.equals(currentUserId)) {
                                            fcmUserInfo.put(userId, userInfo!!)
                                            Log.d("sadfsadfdsafsdaf", fcmUserInfo.toString())

                                        } else {
                                            currentUserNickName = userInfo?.nickName ?: ""
                                        }
                                        partyUserMenuRVA.notifyDataSetChanged()
                                        chattingRVA.notifyDataSetChanged()
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                }
                            )
                    }
                    GroupInfoAdapter()
                    ChattingAdapter()
                    partyUserMenuRVA.notifyDataSetChanged()
                    chattingRVA.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    fun ChattingAdapter() {

        chattingRVA =
            PartyChattingAdapter(chattingRecyclerView,
                this,
                currentUserId.toString(),
                partyId.toString(),
                partyUserInfo, peopleCount)
        chattingRecyclerView.adapter = chattingRVA
        chattingRecyclerView.layoutManager = LinearLayoutManager(this)


    }


    //recyclerview 어댑터
    fun GroupInfoAdapter(
    ) {
        partyUserMenuRVA = PartyUserInfoAdapter(this, partyUserInfoMenu, groupOnerId)
        partyUserMenuRecyclerView.adapter = partyUserMenuRVA
        partyUserMenuRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        partyUserMenuRVA.notifyDataSetChanged()
    }


    fun listener() {
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
            showDetailDialog()

        }
        binding.detailIcon.setOnClickListener {
            showDetailDialog()

        }
        binding.menuPartyOut.setOnClickListener {
            selectPartyLeaderDialog()
            //showExitDialog()
            //recessionParty()
        }

        binding.restaurantIcon.setOnClickListener {
            val url = currentPartyInfo.siteUrls

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }


    }


    //그룹 이름 데이터베이스에서 가져와서 넣기
    fun initGroupName() {
        //그룹 이름 가져오기
        database.child("Groups").child(partyId.toString()).child("groupInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(FirebasePartyInfo::class.java)!!
                    binding.GrpName.setText(item.groupName)
                    binding.partyNameNv.setText(item.groupName)
                    binding.partyMenuNv.setText(item.groupMenu)
                    binding.startDateTextNv.setText(item.startDate)
                    val currentMaxPeople = "${item.curNumberOfPeople} / ${item.maxNumberOfPeople}"
                    binding.currentMaxPeople.setText(currentMaxPeople)
                    binding.detailOnChattingBackgroundText.setText(item.groupDetail)
                    binding.restaruantLocationTextNv.setText(item.restaurantName)
                    groupOnerId = item.groupLeaderId.toString()
                    currentPartyInfo = item
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

    }


    //테스트 임시 클래스
    fun getTime(): String {
        val currentTime = System.currentTimeMillis()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val date = sdf.format(currentTime)
        return date.toString()
    }


    fun sendMessage() {
        val messageText = binding.sendMessage.text.toString()
        if (messageText != "") {
            val group_messages =
                FirebasePartyMessage(currentUserId.toString(), getTime(), messageText, 0)
            database.child("Groups").child(partyId.toString()).child("groupMessages").push()
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

            val databaseRef =
                database.child("Groups").child(partyId.toString()).child("groupMessages")
            val ImgRef = databaseRef.push()
            val uid = ImgRef.key

            data?.data?.let { uri ->
                val storageRef =
                    Firebase.storage.reference.child(partyId.toString()).child("${uid}")
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
                            FirebasePartyMessage(currentUserId.toString(),
                                getTime(),
                                "",
                                1,
                                downloadUrl)
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

    fun recessionParty() {
        retro.recessionParty(1, 1)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {

                    var result: PatchEditPartyInfoResponse? = response.body()
                    Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())

                    if (response.isSuccessful) {
                        Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())

                    } else {
                        Log.d("MKRetrofit", "onResponse 실패: " + response.errorBody().toString())

                        Toast.makeText(this@ChattingAndPartyInfoMFActivity,
                            "그룹 탈퇴 오류 fail",
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
                    Toast.makeText(this@ChattingAndPartyInfoMFActivity,
                        "그룹 탈퇴 오류 ㄴㄻㄴㄹㄴㅇㄹfail",
                        Toast.LENGTH_SHORT)
                        .show()

                }
            })

    }


    fun showDetailDialog() {
        val joinPartyDialog = JoinpartyCustomDialogBinding.inflate(LayoutInflater.from(this))

        dialogBinding(currentPartyInfo, joinPartyDialog)

        val mBuilder = AlertDialog.Builder(this)
            .setView(joinPartyDialog.root)

        mBuilder.show()
    }

    fun dialogBinding(item: FirebasePartyInfo, binding: JoinpartyCustomDialogBinding) {

        //hashtag 보이게하기
        val hashtagList: List<Int> = item.hashTag
        if (hashtagList.isNotEmpty()) {
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

        binding.partyName.text = item.groupName
        binding.partyMenu.text = item.groupMenu
        binding.dateTime.text = item.startDate
        binding.participantsNum.text = " ${item.curNumberOfPeople}  /  ${item.maxNumberOfPeople}"
        binding.restaurantLocation.text = item.restaurantName
        binding.detailText.text = item.groupDetail
//        binding.detailText.text = item.groupDetail

    }


    fun showExitDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("그룹 나가기") // Set the dialog title
        alertDialogBuilder.setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.") // Set the dialog message
        alertDialogBuilder.setPositiveButton("나가기") { dialog, _ ->
            Toast.makeText(this, "positive", Toast.LENGTH_SHORT).show()
        }

        alertDialogBuilder.setNegativeButton("취소") { dialog, _ ->
            Toast.makeText(this, "negative", Toast.LENGTH_SHORT).show()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    fun selectPartyLeaderDialog() {
        val selectPartyLeader = SelectPartyleaderDialogBinding.inflate(LayoutInflater.from(this))

        var copyPartyUserInfoMenu = partyUserInfoMenu
        var notCurrentUserPartyUsers = removeMapByUID(currentUserId, copyPartyUserInfoMenu)

        Log.d("sdfasdsadf", partyUserInfoMenu.toString())
        Log.d("sdfasdsadf", notCurrentUserPartyUsers.toString())

        val recyclerView = selectPartyLeader.recyclerView
        val adapter = SelectPartyLeaderAdapter(this, partyUserInfoMenu, currentUserId)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val mBuilder = AlertDialog.Builder(this)
            .setView(selectPartyLeader.root)
        mBuilder.setTitle("그룹장 선택")
        mBuilder.setPositiveButton("OK") { dialog, _ ->
            // Handle positive button click
        }
        mBuilder.setNegativeButton("Cancel") { dialog, _ ->
            // Handle negative button click
        }

        mBuilder.show()

    }

    fun getMapByUID(uid: String): Map<String, FirebaseUserInfo>? {
        for (map in partyUserInfoMenu) {
            if (map.containsKey(uid)) {
                return map
            }
        }
        return null
    }

    fun replaceMapByUID(uid: String, newMap: Map<String, FirebaseUserInfo>) {
        for (index in 0 until partyUserInfoMenu.size) {
            val map = partyUserInfoMenu[index]
            if (map.containsKey(uid)) {
                partyUserInfoMenu[index] = newMap
                return
            }
        }
    }

    fun removeMapByUID(
        uid: String,
        partyUserInfoMenu: ArrayList<Map<String, FirebaseUserInfo>>,
    ): ArrayList<Map<String, FirebaseUserInfo>> {
        val iterator = partyUserInfoMenu.iterator()
        while (iterator.hasNext()) {
            val map = iterator.next()
            if (map.containsKey(uid)) {
                iterator.remove()
            }
        }
        return partyUserInfoMenu
    }




}


