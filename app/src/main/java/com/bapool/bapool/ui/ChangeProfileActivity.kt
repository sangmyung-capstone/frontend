package com.bapool.bapool.ui

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.bapool.bapool.databinding.ActivityRegisterBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.PatchChangeProfileRequest
import com.bapool.bapool.retrofit.data.PatchChangeProfileResponse
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.fragment.MypageFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangeProfileActivity : AppCompatActivity() {
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
            binding.button6,
            binding.button7,
            binding.button8
        )

        //버튼 선택 이벤트 부여
        for ((index, button) in buttons.withIndex()) {
            button.setOnClickListener {
                selectButton(button, buttons)
                count = index + 1
            }
        }
        //완료 버튼 리스너
        binding.finish.setOnClickListener {
            var nickname: String = textInputEditText.text.toString()
            var userInfo = PatchChangeProfileRequest(nickname, count)

            val retro = ServerRetrofit.create()

            retro.ChangeUserInfo(UserId!!, userInfo)
                .enqueue(object : Callback<PatchChangeProfileResponse> {
                    override fun onResponse(
                        call: Call<PatchChangeProfileResponse>,
                        response: Response<PatchChangeProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            var result: PatchChangeProfileResponse? = response.body()
                            Log.d("bap", "onRequest 성공: $userInfo");
                            Log.d("bap", "onResponse 성공: " + result?.toString());
                            // handle successful response
                            if (result != null) {
                                finish()
                            }
                        } else {
                            // handle error response
                            if (response.code() == 300) {
                                val builder =//닉네임이 중복된다는 다이얼로그 출력
                                    AlertDialog.Builder(this@ChangeProfileActivity).setTitle("")
                                        .setMessage("닉네임이 중복됩니다.")
                                        .setPositiveButton(
                                            "확인",
                                            DialogInterface.OnClickListener { dialog, which ->
                                                Toast.makeText(
                                                    this@ChangeProfileActivity,
                                                    "확인",
                                                    Toast.LENGTH_SHORT
                                                )
                                                    .show()
                                            })
                                builder.show()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<PatchChangeProfileResponse>,
                        t: Throwable
                    ) {
                        // handle network or unexpected error
                    }
                })

        }
    }

    private fun selectButton(selectedButton: ImageButton, buttons: List<ImageButton>) {
        for (button in buttons) {
            if (button == selectedButton) {
                button.setColorFilter(Color.parseColor("#ffff0000"), PorterDuff.Mode.MULTIPLY);
            } else {
                button.setColorFilter(null);
            }
        }
    }
}
