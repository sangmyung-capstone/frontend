package com.bapool.bapool.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.R
import com.bapool.bapool.UserService
import com.bapool.bapool.databinding.ActivityRegisterBinding
import com.bapool.bapool.retrofit.data.UserInfoRequest
import com.bapool.bapool.retrofit.data.UserInfoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    var selectedButton: Button? = null // initially no button is selected
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        var count = 0
        var textInputEditText = binding.nicknameEditText


        setContentView(binding.root);

        val buttons = listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6
        )

        //버튼 선택 이벤트 부여
        for ((index,button) in buttons.withIndex()) {
            button.setOnClickListener {
                selectButton(button, buttons)
                count = index
            }
        }
        //완료 버튼 리스너
        binding.finish.setOnClickListener {
            var nickname: String = textInputEditText.text.toString()
            var userInfo = UserInfoRequest(nickname, count)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://655c8626-5f5d-4846-b60c-20c52d2ea0da.mock.pstmn.io")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userService = retrofit.create(UserService::class.java)
            userService.setUserInfo("accessToken", 1, userInfo)
                .enqueue(object : Callback<UserInfoResponse> {
                    override fun onResponse(
                        call: Call<UserInfoResponse>,
                        response: Response<UserInfoResponse>
                    ) {
                        if (response.isSuccessful) {
                            var result: UserInfoResponse? = response.body()
                            Log.d("bap", "onRequest 성공: $userInfo");
                            Log.d("bap", "onResponse 성공: " + result?.toString());
                            // handle successful response
                            if (result != null) {
                                //중복인 경우
                                if (result.is_duplicate) {
                                    val builder =//닉네임이 중복된다는 다이얼로그 출력
                                        AlertDialog.Builder(this@RegisterActivity).setTitle("")
                                            .setMessage("닉네임이 중복됩니다.")
                                            .setPositiveButton(
                                                "확인",
                                                DialogInterface.OnClickListener { dialog, which ->
                                                    Toast.makeText(
                                                        this@RegisterActivity,
                                                        "확인",
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                })
                                    builder.show()
                                }
                                //중복이 아닌경우 홈화면으로 넘어감
                                else {
                                    val intent =
                                        Intent(this@RegisterActivity, HomeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                }

                            }
                        } else {
                            // handle error response
                        }
                    }
                    override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                        // handle network or unexpected error
                    }
                })

        }
    }

    private fun selectButton(selectedButton: Button, buttons: List<Button>) {
        for (button in buttons) {
            if (button == selectedButton) {
                button.setBackgroundResource(R.drawable.button_border)
            } else {
                button.setBackgroundResource(R.drawable.button_border_blue)
            }
        }
    }
}
