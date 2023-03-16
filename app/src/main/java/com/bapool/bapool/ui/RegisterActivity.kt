package com.bapool.bapool.ui

import android.os.Bundle
import android.widget.Button
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
        var textInputLayout = binding.textInputLayout
        var textInputEditText = binding.nicknameEditText
        var nickname: String = textInputEditText.text.toString()



        setContentView(binding.root);

        val buttons = listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6
        )

        for (button in buttons) {
            button.setOnClickListener {
                selectButton(button, buttons)
                count = button.lineCount
            }
        }
        binding.finish.setOnClickListener {
            var userInfo = UserInfoRequest(nickname,count)
            val retrofit = Retrofit.Builder()
                .baseUrl("https://example.com/api")
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
                            // handle successful response
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
