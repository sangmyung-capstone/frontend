package com.bapool.bapool.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.PartyChattingAdapter
import com.bapool.bapool.databinding.FragmentChattingBinding
import com.bapool.bapool.retrofit.data.FirebasePartyMessage
import com.bapool.bapool.retrofit.data.FirebaseUserInfo
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ChattingFragment : Fragment() {
    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!

    //임시 userId,groupId
    val currentUserId = "userId3"
    val groupId = "groupId1"

    //firebase database
    private lateinit var database: DatabaseReference

    //RecyclerView adapter 연결
    private lateinit var chattingRVA: PartyChattingAdapter
    lateinit var chattingRecyclerView: RecyclerView
    var partyUserInfo: MutableMap<String, FirebaseUserInfo> = HashMap()
    var peopleCount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentChattingBinding.inflate(inflater, container, false)

        initializeVari()
        getPartyUserInfo()
        listener()
        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        database = Firebase.database.reference
        chattingRecyclerView = binding.chattingRv
    }

    //버튼 리스너
    fun listener() {
        //프래그먼트 이동버튼
        binding.groupFragment.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_chattingFragment_to_groupInfoGragment)
        }
        //메시지 버튼 클릭
        binding.sendIcon.setOnClickListener {
            sendMessage()
        }
        //사진 채팅창에 등록 버튼
        binding.sendImgBtn.setOnClickListener {
            saveImg()
        }

    }

    //메시지 버튼 누르면 메시지가 데이터베이스에 저장
    fun sendMessage() {
        val messageText = binding.sendMessage.text.toString()
        if (messageText != "") {
            val group_messages =
                FirebasePartyMessage(currentUserId, getTime(), messageText, 0)
            database.child("Groups").child(groupId).child("groupMessages").push()
                .setValue(group_messages)
                .addOnSuccessListener {
                    binding.sendMessage.text.clear()
                }
        }


    }

    fun saveImg() {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, 100)
    }


    //현재시각 가져오기
    fun getTime(): String {
        val currentTime = System.currentTimeMillis()

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val date = sdf.format(currentTime)
        return date.toString()
    }

    //recyclerview Adapter
    fun adapter() {
        chattingRVA =
            PartyChattingAdapter(chattingRecyclerView,
                requireContext(),
                currentUserId,
                groupId,
                partyUserInfo, peopleCount)
        chattingRecyclerView.adapter = chattingRVA
        chattingRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }


    fun getPartyUserInfo() {

        FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
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
                    adapter()
                    chattingRVA.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("생명주기", "onDestroyView")
        _binding = null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == 100) {

            val databaseRef = database.child("Groups").child(groupId).child("groupMessages")
            val ImgRef = databaseRef.push()
            val uid = ImgRef.key

            data?.data?.let { uri ->
                val storageRef = Firebase.storage.reference.child(groupId).child("${uid}")
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
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
                    }
                }
            }


        }
    }


//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        Log.d("생명주기","onAttach")
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        Log.d("생명주기","onViewCreated")
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        Log.d("생명주기","onViewStateRestored")
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        Log.d("생명주기","onStart")
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        Log.d("생명주기","onResume")
//    }
//
//    override fun onPause() {
//        super.onPause()
//        chattingRVA.removeValueEventListener()
//        Log.d("생명주기","onPause")
//        Log.d("생명주기","PAUSE!!!!")
//    }
//
//    override fun onStop() {
//        super.onStop()
//        chattingRVA.removeValueEventListener()
//        Log.d("생명주기","onStop")
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        chattingRVA.removeValueEventListener()
//        Log.d("생명주기","onDestroy")
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        Log.d("생명주기","onDetach")
//    }

}