package com.bapool.bapool.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.MyPartyListAdapter
import com.bapool.bapool.databinding.FragmentPartyBinding
import com.bapool.bapool.retrofit.data.MyPartyListModel
import com.bapool.bapool.ui.RestaurantPartyActivity
import java.time.LocalDateTime


class PartyFragment : Fragment() {
    private var _binding: FragmentPartyBinding? = null
    private val binding get() = _binding!!
    private lateinit var myGroupAdapter: MyPartyListAdapter
    lateinit var myGroupRv: RecyclerView
    var myGrpListModel = arrayListOf<MyPartyListModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPartyBinding.inflate(inflater, container, false)

        dummyData()
        initializeVari()
        listener()
        adapter()

        return binding.root
    }

    //변수 초기화
    fun initializeVari() {
        myGroupRv = binding.myGroupListRv
        myGroupAdapter = MyPartyListAdapter(requireContext(), myGrpListModel)

    }

    //리스너 호출
    fun listener() {
        binding.goToResGrp.setOnClickListener {
            val intent = Intent(requireContext(), RestaurantPartyActivity::class.java)
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
        myGrpListModel.clear()
        myGrpListModel.add(
            MyPartyListModel(
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
            MyPartyListModel(
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