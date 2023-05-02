package com.bapool.bapool.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bapool.bapool.R
import com.bapool.bapool.databinding.FragmentGroupInfoFragmentBinding

class GroupInfoFragment : Fragment() {
    private var _binding: FragmentGroupInfoFragmentBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentGroupInfoFragmentBinding.inflate(inflater, container, false)


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

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}