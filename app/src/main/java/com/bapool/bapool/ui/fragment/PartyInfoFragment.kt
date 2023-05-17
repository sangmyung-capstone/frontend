package com.bapool.bapool.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bapool.bapool.R
import com.bapool.bapool.databinding.FragmentPartyInfoFragmentBinding
import com.bapool.bapool.retrofit.data.FirebasePartyInfo
import com.bapool.bapool.ui.PartyInfoViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PartyInfoFragment : Fragment() {
    //viewbinding
    private var _binding: FragmentPartyInfoFragmentBinding? = null
    private val binding get() = _binding!!


    //realtime database
    private lateinit var database: DatabaseReference

    //viewModel in ChattingAndPartyInfoActivity
    private lateinit var partyInfoViewModel: PartyInfoViewModel

    //임시 userId,groupId
    val currentUserId = "userId1"
    val groupId = "groupId1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentPartyInfoFragmentBinding.inflate(inflater, container, false)
        database = Firebase.database.reference
        partyInfoViewModel = ViewModelProvider(this).get(PartyInfoViewModel::class.java)
        val liveData = partyInfoViewModel.getData()
        partyInfoViewModel.liveData.observe(viewLifecycleOwner, Observer {
            Log.d("initGroupName", liveData.value.toString())
        })
        Log.d("initGroupName", liveData.value.toString())
        getGroupInfo()
        listener()
        return binding.root
    }

    fun listener() {
        //프래그먼트 이동 버튼
        binding.chatFragment.setOnClickListener {
            it.findNavController()
                .navigate(R.id.action_groupInfoGragment_to_chattingFragment)
        }

    }

    //그룹 정보 표시.  start_date 랑 end_date 가공 필요
    fun getGroupInfo() {
//        val partyInfo: FirebasePartyInfo = partyInfoViewModel.partyInfoViewModel

//        val partyInfo = partyInfoViewModel.liveData.value as FirebasePartyInfo
//        binding.groupNameText.setText(partyInfo.groupName)
//        binding.groupMenuText.setText(partyInfo.groupMenu)
//        binding.groupDetailText.setText(partyInfo.groupDetail)
//        binding.groupMaxPeopleText.setText(partyInfo.maxNumberOfPeople.toString())
//        binding.groupPeopleText.setText(partyInfo.curNumberOfPeople.toString())
//        binding.groupDateText.setText(partyInfo.startDate)
//        binding.groupTimeText.setText(partyInfo.endDate)
//        Log.d("initGroupName",partyInfoViewModel.toString())


//
//        database.child("Groups").child(groupId).child("groupInfo")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//
//                    val item = dataSnapshot.getValue(FirebasePartyInfo::class.java)!!
//                    Log.d("GroupInfoData", item.toString())
//                    binding.groupNameText.setText(item.groupName)
//                    binding.groupMenuText.setText(item.groupMenu)
//                    binding.groupDetailText.setText(item.groupDetail)
//                    binding.groupMaxPeopleText.setText(item.maxNumberOfPeople.toString())
//                    binding.groupPeopleText.setText(item.curNumberOfPeople.toString())
//                    binding.groupDateText.setText(item.startDate)
//                    binding.groupTimeText.setText(item.endDate)
//
//                }
//}
//        override fun onCancelled(databaseError: DatabaseError) {
//
//        }
//    })


}

//뷰바인딩 생명주기 관리
override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}


}

