package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.ActivityCheckUserProfileBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckUserProfileActivity : AppCompatActivity() {
    //binding
    private lateinit var binding: ActivityCheckUserProfileBinding

    //레트로핏
    val retro = ServerRetrofit.create()

    //상대 uid
    var opponentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        opponentUserId = intent.getStringExtra("opponentUserId")!!
        checkUserProfileRetrofit()

        //이미지 binding


    }

    fun checkUserProfileRetrofit() {
        retro.checkUserProfile(UserId!!.toLong(), opponentUserId.toLong())
            .enqueue(object : Callback<CheckUserProfileResponse> {
                override fun onResponse(
                    call: Call<CheckUserProfileResponse>,
                    response: Response<CheckUserProfileResponse>,
                ) {

                    if (response.isSuccessful) {
                        val result = response.body()
                        val userInfo = result!!.result
                        val imageName = "image${userInfo.profileImg}"
                        val resourceId = resources.getIdentifier(imageName, "drawable", packageName)
                        binding.userImage.setImageResource(resourceId)

                        //평점 binding
                        if (result.result.rating.toFloat() < 1.0 && result.result.rating.toFloat() >= 0.0) {
                            binding.angry.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.main
                                )
                            )
                        } else if (result.result.rating.toFloat() < 2.0 && result.result.rating.toFloat() >= 1.0) {
                            binding.sad.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.main
                                )
                            )
                        } else if (result.result.rating.toFloat() < 3.0 && result.result.rating.toFloat() >= 2.0) {
                            binding.meh.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.main
                                )
                            )
                        } else if (result.result.rating.toFloat() < 4.0 && result.result.rating.toFloat() >= 3.0) {
                            binding.smile.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.main
                                )
                            )
                        } else if (result.result.rating.toFloat() < 5.0 && result.result.rating.toFloat() >= 4.0) {
                            binding.happy.setColorFilter(
                                ContextCompat.getColor(
                                    context,
                                    R.color.main
                                )
                            )
                        }


                        //hashtag binding
                        if (userInfo.hashtag != null) {
                            if (userInfo.hashtag.isNotEmpty()) {
                                binding.hashtagVisible.visibility = View.VISIBLE
                                binding.talkcount.text =
                                    result.result.hashtag.find { it.hashtag_id == 1 }?.count.toString()
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
                        }
                        if (UserId.toString() == opponentUserId) {
                            binding.banBtn.visibility = View.GONE
                        }
                        if (userInfo.is_block) {
                            binding.banBtn.text = "차단 취소"
                            binding.banBtn.setBackgroundResource(R.drawable.unblock_button_background)
                        } else {
                            binding.banBtn.text = "차단"
                            binding.banBtn.setBackgroundResource(R.drawable.block_button_background)
                        }
                        binding.banBtn.setOnClickListener {
                            if (userInfo.is_block) {
                                binding.banBtn.text = "차단"
                                binding.banBtn.setBackgroundResource(R.drawable.block_button_background)
                                userInfo.is_block = false
                                blockRetrofit()
                            } else {
                                binding.banBtn.text = "차단 취소"
                                binding.banBtn.setBackgroundResource(R.drawable.unblock_button_background)
                                userInfo.is_block = true
                                blockRetrofit()
                            }
                        }

                    } else {
                    }
                }

                override fun onFailure(call: Call<CheckUserProfileResponse>, t: Throwable) {
                    Log.d("checkUserProfileRetrofit", t.toString())

                }
            })

    }


    fun blockRetrofit() {

        val blockUserInfo = BlockUserRequest(opponentUserId.toLong())

        retro.BlockUser(UserId!!, blockUserInfo)
            .enqueue(object : Callback<BlockUserChattingProfileResponse> {
                override fun onResponse(
                    call: Call<BlockUserChattingProfileResponse>,
                    response: Response<BlockUserChattingProfileResponse>,
                ) {
                    if (response.isSuccessful) {
                    } else {
                    }
                }

                override fun onFailure(call: Call<BlockUserChattingProfileResponse>, t: Throwable) {
                }
            })
    }
}