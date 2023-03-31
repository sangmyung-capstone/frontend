package com.bapool.bapool.ui.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.FragmentMypageBinding
import com.bapool.bapool.retrofit.data.DeleteUserResponse
import com.bapool.bapool.retrofit.data.GetMypageResponse
import com.bapool.bapool.retrofit.data.PostRegisterResponse
import com.bapool.bapool.retrofit.data.Restaurant
import com.bapool.bapool.ui.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        //통신과정
        val retro = RetrofitService.create()

        retro.getMyPage("accessToken", 1)
            .enqueue(object : Callback<GetMypageResponse> {
                override fun onResponse(
                    call: Call<GetMypageResponse>,
                    response: Response<GetMypageResponse>
                ) {
                    if (response.isSuccessful) {
                        var result: GetMypageResponse? = response.body()
                        Log.d("bap", "onResponse 성공: " + result?.toString())
                        // handle successful response
                        if (result != null) {
                            when (result.result.profileImg) {
                                1 -> binding.profileimg.setImageResource(R.drawable.image1)
                                2 -> binding.profileimg.setImageResource(R.drawable.image2)
                                3 -> binding.profileimg.setImageResource(R.drawable.image3)
                                4 -> binding.profileimg.setImageResource(R.drawable.image4)
                                5 -> binding.profileimg.setImageResource(R.drawable.image5)
                                6 -> binding.profileimg.setImageResource(R.drawable.image6)
                                7 -> binding.profileimg.setImageResource(R.drawable.image7)
                                8 -> binding.profileimg.setImageResource(R.drawable.image8)
                            }
                            binding.nickname.text = result.result.nickname
                            binding.rating.rating = result.result.rating.toFloat()
                        }
                    } else {
                        // handle error response
                    }
                }

                override fun onFailure(call: Call<GetMypageResponse>, t: Throwable) {
                    // handle network or unexpected error
                }
            })
        //통신과정

        listener()

        return binding.root
    }


    //리스너 호출
    fun listener() {

        binding.logout.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        binding.changeProfile.setOnClickListener {
            val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        binding.deleteUser.setOnClickListener {
            val retro = RetrofitService.create()

            retro.DeleteUser("accessToken", 1)
                .enqueue(object : Callback<DeleteUserResponse> {
                    override fun onResponse(
                        call: Call<DeleteUserResponse>,
                        response: Response<DeleteUserResponse>
                    ) {
                        if (response.isSuccessful) {
                            var result: DeleteUserResponse? = response.body()
                            Log.d("bap", "onResponse 성공: " + result?.toString())
                            // handle successful response

                        } else {
                            // handle error response
                        }
                    }

                    override fun onFailure(call: Call<DeleteUserResponse>, t: Throwable) {
                        // handle network or unexpected error
                    }
                })
            //통신과정

        }
        binding.block.setOnClickListener {
            val intent = Intent(requireContext(), BlockListActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        binding.restarurantlog.setOnClickListener {
            val intent = Intent(requireContext(), RestaurantLogActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
    }


    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}