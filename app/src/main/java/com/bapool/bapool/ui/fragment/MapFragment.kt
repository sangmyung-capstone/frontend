package com.bapool.bapool.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bapool.bapool.R
import com.bapool.bapool.databinding.FragmentMapBinding

class MapFragment : Fragment() {
    private var _binding : FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater,container,false)

        listener()

        return binding.root
    }

    //리스너 호출
    fun listener(){
        binding.fragmentGroup.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapFragment_to_groupFragment)
        }
        binding.fragmentMypage.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapFragment_to_mypageFragment)
        }
    }

    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}