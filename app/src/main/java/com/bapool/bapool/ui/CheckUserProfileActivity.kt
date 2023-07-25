package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.ActivityCheckUserProfileBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckUserProfileActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityCheckUserProfileBinding

    //레트로핏
    val retro = ServerRetrofit.create()

    //opponentUid 와 myUid를 받아와야함. myUid는 companion으로 받아오면 되고, opponentUid는 intent로 받아와서 retrofit으로 날리면됨
    //내 userId
    val myUid = 11

    //상대 uid
    val opponentUid: Long = 2
    val blockUserInfo = BlockUserRequest(opponentUid)
    var opponentUserId: String =""

    //retrofit 연결 전 더미데이터
    val dummyHashtagData = listOf<Int>(3, 4)
    val dummyData = CheckUserProfileResult(
        1, 3, "최부장0923",false ,2.5,  dummyHashtagData
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        opponentUserId = intent.getStringExtra("opponentUserId").toString()
        checkUserProfileRetrofit()

        //이미지 binding
        val imageName = "image${dummyData.profileImg}"
        val resourceId =
            this.resources.getIdentifier(imageName,
                "drawable",
                this.packageName)
        binding.userImage.setImageResource(resourceId)

        //평점 binding
        binding.rating.rating = dummyData.rating.toFloat()


        //hashtag binding
        if (dummyData.hashtag != null) {
            if (dummyData.hashtag.isNotEmpty()) {
                binding.hashtagVisible.visibility = View.VISIBLE
                for (item in dummyData.hashtag) {
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
        if (dummyData.is_block) {
            binding.banBtn.text = "차단 취소"
            binding.banBtn.setBackgroundResource(R.drawable.unblock_button_background)
        } else {
            binding.banBtn.text = "차단"
            binding.banBtn.setBackgroundResource(R.drawable.block_button_background)
        }
        binding.banBtn.setOnClickListener {
            if (dummyData.is_block) {
                binding.banBtn.text = "차단"
                binding.banBtn.setBackgroundResource(R.drawable.block_button_background)
                dummyData.is_block = false
                blockRetrofit()
            } else {
                binding.banBtn.text = "차단 취소"
                binding.banBtn.setBackgroundResource(R.drawable.unblock_button_background)
                dummyData.is_block = true
                blockRetrofit()
            }
        }


    }

    fun checkUserProfileRetrofit() {

        retro.checkUserProfile(3).enqueue(object : Callback<CheckUserProfileResponse>{
            override fun onResponse(
                call: Call<CheckUserProfileResponse>,
                response: Response<CheckUserProfileResponse>,
            ) {

                if (response.isSuccessful) {
                    val result = response.body()
                    Log.d("checkUserProfile1232", response.body().toString())
                } else {
                    Log.d("checkUserProfile1232", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<CheckUserProfileResponse>, t: Throwable) {
                Log.d("checkUserProfile1232","실패")
            }
        })

    }


    fun blockRetrofit() {

        retro.BlockUser(5, blockUserInfo)
            .enqueue(object : Callback<BlockUserChattingProfileResponse> {
                override fun onResponse(
                    call: Call<BlockUserChattingProfileResponse>,
                    response: Response<BlockUserChattingProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                        Log.d("BlockUserCheck", response.body().toString())
                    } else {
                        Log.d("BlockUserCheck", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<BlockUserChattingProfileResponse>, t: Throwable) {
                    Log.d("BlockUserCheck", t.toString())
                }
            })
    }
}