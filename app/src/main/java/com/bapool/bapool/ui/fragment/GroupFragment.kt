package com.bapool.bapool.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.MyGrpListAdapter
import com.bapool.bapool.databinding.FragmentGroupBinding
import com.bapool.bapool.retrofit.data.MyGrpListModel
import com.bapool.bapool.ui.ResGrpActivity
import java.time.LocalDateTime


class GroupFragment : Fragment() {
    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding!!
    private lateinit var myGroupAdapter: MyGrpListAdapter
    lateinit var myGroupRv: RecyclerView
    var myGrpListModel = arrayListOf<MyGrpListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGroupBinding.inflate(inflater, container, false)

        dummyData()
        initializeVari()
        listener()
        adapter()

        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        myGroupRv = binding.myGroupListRv
        myGroupAdapter = MyGrpListAdapter(requireContext(), myGrpListModel)

    }

    //리스너 호출
    fun listener() {
        binding.fragmentMypage.setOnClickListener {
            it.findNavController().navigate(R.id.action_groupFragment_to_mypageFragment)
        }
        binding.fragmentMap.setOnClickListener {
            it.findNavController().navigate(R.id.action_groupFragment_to_mapFragment)
        }
        binding.goToResGrp.setOnClickListener {
            val intent = Intent(requireContext(), ResGrpActivity::class.java)
            startActivity(intent)
        }
    }

    //recyclerView adapter
    fun adapter() {
        myGroupRv.adapter = myGroupAdapter
        myGroupRv.layoutManager = LinearLayoutManager(requireContext())
    }

    //retrofit 연결 전 dummydata
    fun dummyData() {
        myGrpListModel.add(
            MyGrpListModel(
                123,
                "학식",
                "학식같이먹을사람!",
                3,
                4,
                LocalDateTime.of(2023, 3, 10, 12, 10, 40),
                LocalDateTime.of(2023, 3, 10, 12, 59, 10),
                "첫번째 그룹 dㅌㅊㅍㅋㄴㅇㄶㄴㅇㄹ어러ㅠㅏ;ㅣㅓ아ㅣㅠㅗㅓ이ㅏㅗㅠㅓㅇ라ㅣㅓㅠㅏㅣㅇ러ㅚㅏㅇ러ㅣㅏㅇ러ㅚㅏ어ㅚㅏㅇ러ㅗㅇ러ㅚ어리ㅏㅗㅓㅇ리ㅏㅗㅇㄹummyData",
                3
            )
        )
        myGrpListModel.add(
            MyGrpListModel(
                12, "학식", "학식같이먹을사람!", 2, 10,
                LocalDateTime.of(2023, 3, 10, 11, 10, 40),
                LocalDateTime.of(2023, 3, 10, 15, 59, 10), "두번째 그룹 dummyData", 10
            )
        )
    }

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}