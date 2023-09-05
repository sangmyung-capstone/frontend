package com.bapool.bapool.ui.fragment

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bapool.bapool.R
import com.bapool.bapool.databinding.FragmentMypageBinding
import com.bapool.bapool.preference.MyApplication
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.DeleteUserResponse
import com.bapool.bapool.retrofit.data.GetMypageResponse
import com.bapool.bapool.ui.BlockListActivity
import com.bapool.bapool.ui.ChangeProfileActivity
import com.bapool.bapool.ui.LoginActivity
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.RestaurantLogActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MypageFragment : Fragment() {
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    val TAG = "MypageFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)

        setResultNext()
        //통신과정
        val retro = ServerRetrofit.create()

        retro.getMyPage(UserId!!)
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
                            if (result.result.rating.toFloat() < 1.0 && result.result.rating.toFloat() >= 0.0) {
                                binding.angry.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main))
                            } else if (result.result.rating.toFloat() < 2.0 && result.result.rating.toFloat() >= 1.0) {
                                binding.sad.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main))
                            } else if (result.result.rating.toFloat() < 3.0 && result.result.rating.toFloat() >=2.0) {
                                binding.meh.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main))
                            } else if(result.result.rating.toFloat() < 4.0 && result.result.rating.toFloat() >=3.0){
                                binding.smile.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main))
                            } else if(result.result.rating.toFloat() < 5.0 && result.result.rating.toFloat() >= 4.0){
                                binding.happy.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main))
                            }
                            binding.nickname.text = result.result.nickname

                            binding.talkcount.text =
                                result.result?.hashtag?.find { it.hashtag_id == 1 }?.count.toString()
                            binding.kindcount.text =
                                result.result.hashtag.find { it.hashtag_id == 2 }?.count.toString()
                            binding.mannercount.text =
                                result.result.hashtag.find { it.hashtag_id == 3 }?.count.toString()
                            binding.quietcount.text =
                                result.result.hashtag.find { it.hashtag_id == 4 }?.count.toString()
                            if (binding.talkcount.text.equals("null")) {
                                binding.talkcount.text = "0"
                            }
                            if (binding.kindcount.text.equals("null")) {
                                binding.kindcount.text = "0"
                            }
                            if (binding.mannercount.text.equals("null")) {
                                binding.mannercount.text = "0"
                            }
                            if (binding.quietcount.text.equals("null")) {
                                binding.quietcount.text = "0"
                            }

                        }
                    } else {
                        Log.d("bap", "onResponse 실패: " + response)
                        // handle error response
                    }
                }

                override fun onFailure(call: Call<GetMypageResponse>, t: Throwable) {
                    Log.d("bap", "onResponse 실패: $call $t")

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
            MyApplication.prefs.setString("prefstoken", "")
            MyApplication.prefs.setString("prefsid", "")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        }
        binding.changeProfile.setOnClickListener {
            val intent = Intent(requireContext(), ChangeProfileActivity::class.java)
            resultLauncher.launch(intent)
        }
        binding.deleteUser.setOnClickListener {
            val retro = ServerRetrofit.create()

            retro.DeleteUser(UserId!!)
                .enqueue(object : Callback<DeleteUserResponse> {
                    override fun onResponse(
                        call: Call<DeleteUserResponse>,
                        response: Response<DeleteUserResponse>
                    ) {
                        if (response.isSuccessful) {
                            var result: DeleteUserResponse? = response.body()
                            Log.d("bap", "onResponse 성공: " + result?.toString())
                            // handle successful response
                            val intent = Intent(requireContext(), LoginActivity::class.java)
                            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))


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

    private fun setResultNext() {
        resultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                binding.nickname.text = result.data?.getStringExtra("nickname")
                when (result.data?.getIntExtra("profileimg", 1)) {
                    1 -> binding.profileimg.setImageResource(R.drawable.image1)
                    2 -> binding.profileimg.setImageResource(R.drawable.image2)
                    3 -> binding.profileimg.setImageResource(R.drawable.image3)
                    4 -> binding.profileimg.setImageResource(R.drawable.image4)
                    5 -> binding.profileimg.setImageResource(R.drawable.image5)
                    6 -> binding.profileimg.setImageResource(R.drawable.image6)
                    7 -> binding.profileimg.setImageResource(R.drawable.image7)
                    8 -> binding.profileimg.setImageResource(R.drawable.image8)
                }
                //여기에 프래그먼트 새로고침
            }
        }
    }


    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}