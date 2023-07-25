package com.bapool.bapool.ui

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.PartyChattingAdapter
import com.bapool.bapool.adapter.PartyUserInfoAdapter
import com.bapool.bapool.adapter.SelectPartyLeaderAdapter
import com.bapool.bapool.databinding.ActivityChattingAndPartyInfoMfactivityBinding
import com.bapool.bapool.databinding.JoinpartyCustomDialogBinding
import com.bapool.bapool.databinding.SelectPartyleaderDialogBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.FirebasePartyInfo
import com.bapool.bapool.retrofit.data.FirebasePartyMessage
import com.bapool.bapool.retrofit.data.FirebaseUserInfo
import com.bapool.bapool.retrofit.data.PatchEditPartyInfoResponse
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
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
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

    //알람시스템 변수
    lateinit var startdate: String
    var mypartyid: Int = 0
    lateinit var partyName: String


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
                                            fcmUserInfo.put(userId, userInfo)
                                        } else {
                                            currentUserNickName = userInfo.nickName
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
            PartyChattingAdapter(
                chattingRecyclerView,
                this,
                currentUserId.toString(),
                partyId.toString(),
                partyUserInfo, peopleCount
            )
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

    fun GroupInfoAdapterSort() {

    }


    fun listener() {
        binding.sendIcon.setOnClickListener {
            sendMessage()
        }
        //사진 채팅창에 등록 버튼
        binding.sendImgBtn.setOnClickListener {
            saveImg()

        }

        toggle = ActionBarDrawerToggle(
            this@ChattingAndPartyInfoMFActivity,
            binding.drawerNavigationLayout,
            R.string.open,
            R.string.close
        )
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
            showExitDialog()
        }

        binding.restaurantIcon.setOnClickListener {
            val url = currentPartyInfo.siteUrls

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://${url}"))
            startActivity(intent)
        }

        binding.closePartyBtn.setOnClickListener {
            closePartyDialog()
        }


    }


    //그룹 이름 데이터베이스에서 가져와서 넣기
    fun initGroupName() {
        //그룹 이름 가져오기
        database.child("Groups").child(partyId.toString()).child("groupInfo")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val item = snapshot.getValue(FirebasePartyInfo::class.java)!!
                    binding.GrpName.text = item.groupName
                    binding.partyNameNv.text = item.groupName
                    binding.partyMenuNv.text = item.groupMenu
                    binding.startDateTextNv.text = item.startDate
                    startdate = item.startDate
                    mypartyid = partyId.toInt()
                    partyName = item.groupName
                    val currentMaxPeople = "${item.curNumberOfPeople} / ${item.maxNumberOfPeople}"
                    binding.currentMaxPeople.text = currentMaxPeople
                    binding.detailOnChattingBackgroundText.text = item.groupDetail
                    binding.restaruantLocationTextNv.text = item.restaurantName
                    if (currentUserId == item.groupLeaderId.toString()) {
                        binding.closePartyBtn.visibility = View.VISIBLE
                    }
                    if (item.hashTag != null) {
                        if (item.hashTag.isNotEmpty()) {
                            binding.hashtagVisible.visibility = View.VISIBLE
                            for (item in item.hashTag) {
                                when (item) {
                                    1 -> binding.hash1.visibility = View.VISIBLE
                                    2 -> binding.hash2.visibility = View.VISIBLE
                                    3 -> binding.hash3.visibility = View.VISIBLE
                                    4 -> binding.hash4.visibility = View.VISIBLE
                                    5 -> binding.hash5.visibility = View.VISIBLE
                                }
                            }
                        }
                    }

                    groupOnerId = item.groupLeaderId.toString()
                    currentPartyInfo = item

                    //알람 만들기

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
                            FirebasePartyMessage(
                                currentUserId.toString(),
                                getTime(),
                                "",
                                1,
                                downloadUrl
                            )
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
        intent.putExtra("partyInfo", currentPartyInfo)
        startActivity(intent)
    }

    fun recessionParty() {
        retro.recessionParty(2, 2)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {

//                    var result: PatchEditPartyInfoResponse? = response.body()

                    if (response.isSuccessful) {

                    } else {
                        Toast.makeText(
                            this@ChattingAndPartyInfoMFActivity,
                            "response x",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ChattingAndPartyInfoMFActivity,
                        "그룹 탈퇴 오류 ㄴㄻㄴㄹㄴㅇㄹfail",
                        Toast.LENGTH_SHORT
                    )
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
    }


    fun showExitDialog() {

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("파티 나가기") // Set the dialog title
        alertDialogBuilder.setMessage("나가기를 하면 대화내용이 모두 삭제되고 채팅목록에서도 삭제됩니다.") // Set the dialog message
        alertDialogBuilder.setPositiveButton("나가기") { dialog, _ ->
            if (currentUserId.equals(currentPartyInfo.groupLeaderId.toString())) {
                selectPartyLeaderDialog()
            } else {
                recessionParty()
            }
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

        val recyclerView = selectPartyLeader.recyclerView
        val adapter = SelectPartyLeaderAdapter(this, partyUserInfoMenu, currentUserId, partyId)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        val mBuilder = AlertDialog.Builder(this)
            .setView(selectPartyLeader.root)
        mBuilder.setTitle("파티장 선택")
        mBuilder.setPositiveButton("OK") { dialog, _ ->
            showExitDialog()
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


    fun closePartyRetrofit() {
        retro.closeParty(currentUserId.toLong(), partyId.toLong())
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("closeParty", response.body().toString())
                        //스타트데이트로 알림 생성
                        closePartyConfirmDialog()
                        callAlarm(
                            this@ChattingAndPartyInfoMFActivity,
                            startdate,
                            partyId.toInt(),
                            "$partyName 에서 곧 먹을 시간입니다."
                        )
                    } else {
                        Log.d("closeParty", response.errorBody().toString())

                    }
                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
                    Log.d("closeParty", "실패")
                }
            })

    }

    fun closePartyDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("파티를 마감하시겠습니까? 확정된 시간은 변경할 수 없습니다.") // Set the dialog message
        alertDialogBuilder.setPositiveButton("확인") { dialog, _ ->
            closePartyRetrofit()
        }
        alertDialogBuilder.setNegativeButton("취소") { dialog, _ ->
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun closePartyConfirmDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("파티가 마감되었습니다.") // Set the dialog message

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun callAlarm(context: Context, time: String, alarm_code: Int, content: String) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val receiverIntent = Intent(context, MyReceiver::class.java) //리시버로 전달될 인텐트 설정
        receiverIntent.apply {
            putExtra("alarm_rqCode", alarm_code) //요청 코드를 리시버에 전달
            putExtra("content", content) //수정_일정 제목을 리시버에 전달
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                context,
                alarm_code,
                receiverIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                context,
                alarm_code,
                receiverIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd H:mm:ss")
        var datetime = Date()
        try {
            datetime = dateFormat.parse(time) as Date
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        val calendar = Calendar.getInstance()
        calendar.time = datetime
        calendar.add(Calendar.HOUR_OF_DAY, -2)
        Log.d("알림","$calendar")

        //API 23(android 6.0) 이상(해당 api 레벨부터 도즈모드 도입으로 setExact 사용 시 알람이 울리지 않음)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }


}


