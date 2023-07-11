package com.bapool.bapool.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.ActivityRegisterBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserToken
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    var selectedButton: Button? = null // initially no button is selected
    private lateinit var firebasetoken: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        var count = 0
        var textInputEditText = binding.nicknameEditText

        setContentView(binding.root);
        FirebaseMessaging.getInstance().token.addOnCompleteListener (
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("dsksdfkjsfkj", "token.toString()")
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                firebasetoken = task.result
            })

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
                count = index
            }
        }
        //완료 버튼 리스너
        binding.finish.setOnClickListener {
            if (intent.getStringExtra("company").equals("naver")) {
                var nickname: String = textInputEditText.text.toString()
                var userInfo =
                    PostNaverSignupRequest(
                        intent.getStringExtra("token").toString(),
                        nickname,
                        count,
                        firebasetoken
                    )
                Log.d("bap", "OnRequest 정보 $userInfo")

                //    val retro = RetrofitService.create()    // Mock Server
                val retro = ServerRetrofit.create()

                retro.PostNaverSignup(userInfo)
                    .enqueue(object : Callback<PostNaverSignupResponse> {
                        override fun onResponse(
                            call: Call<PostNaverSignupResponse>,
                            response: Response<PostNaverSignupResponse>,
                        ) {
                            if (response.isSuccessful) {
                                var result: PostNaverSignupResponse? = response.body()
                                Log.d("bap", "onRequest 성공: $userInfo");
                                Log.d("bap", "onResponse 성공: " + result?.toString());
                                // handle successful response
                                if (result != null) {
                                    //중복인 경우
                                    if (result.code == 300) {
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
                                        val token = PostNaverSigninRequest(
                                            intent.getStringExtra("token").toString()
                                        )

                                        retro.PostNaverSingin(token)
                                            .enqueue(object : Callback<PostNaverSigninResponse> {
                                                override fun onResponse(
                                                    call: Call<PostNaverSigninResponse>,
                                                    response: Response<PostNaverSigninResponse>,
                                                ) {
                                                    if (response.isSuccessful) {
                                                        var result: PostNaverSigninResponse? =
                                                            response.body()
                                                        if (result != null) {
                                                            UserToken = result.result.access_token
                                                            LoginActivity.UserId =
                                                                result.result.user_id
                                                        }
                                                        Log.d("bap", "onRequest 성공: $userInfo");
                                                        Log.d(
                                                            "bap",
                                                            "onResponse 성공: " + result?.toString()
                                                        );
                                                        // handle successful response
                                                        val intent =
                                                            Intent(
                                                                this@RegisterActivity,
                                                                FinishActivity::class.java
                                                            )
                                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                                        finish()
                                                    } else {
                                                        // handle error response
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<PostNaverSigninResponse>,
                                                    t: Throwable
                                                ) {
                                                    // handle network or unexpected error
                                                }
                                            })
                                    }

                                }
                            } else {
                                // handle error response
                            }
                        }

                        override fun onFailure(call: Call<PostNaverSignupResponse>, t: Throwable) {
                            // handle network or unexpected error
                        }
                    })
            } else if (intent.getStringExtra("company").equals("kakao")) {

                var nickname: String = textInputEditText.text.toString()
                var userInfo =
                    PostKakaoSignupRequest(
                        intent.getStringExtra("token").toString(),
                        nickname,
                        count,
                        firebasetoken
                    )
                Log.d("bap", "OnRequest 정보 $userInfo")

//                val retro = RetrofitService.create()    // Mock Server
                val retro = ServerRetrofit.create()

                retro.PostKakaoSignup(userInfo)
                    .enqueue(object : Callback<PostKakaoSignupResponse> {
                        override fun onResponse(
                            call: Call<PostKakaoSignupResponse>,
                            response: Response<PostKakaoSignupResponse>,
                        ) {
                            if (response.isSuccessful) {
                                var result: PostKakaoSignupResponse? = response.body()
                                Log.d("bap", "onRequest 성공: $userInfo");
                                Log.d("bap", "onResponse 성공: " + result?.toString());
                                // handle successful response
                                if (result != null) {
                                    //중복인 경우
                                    if (result.code == 300) {
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
                                        val token = PostkakaoSigninRequest(
                                            intent.getStringExtra("token").toString()
                                        )

                                        retro.PostKakaoSingin(token)
                                            .enqueue(object : Callback<PostKakaoSigninResponse> {
                                                override fun onResponse(
                                                    call: Call<PostKakaoSigninResponse>,
                                                    response: Response<PostKakaoSigninResponse>,
                                                ) {
                                                    if (response.isSuccessful) {
                                                        var result: PostKakaoSigninResponse? =
                                                            response.body()
                                                        if (result != null) {
                                                            UserToken = result.result.access_token
                                                            LoginActivity.UserId =
                                                                result.result.user_id
                                                        }
                                                        Log.d("bap", "onRequest 성공: $userInfo");
                                                        Log.d(
                                                            "bap",
                                                            "onResponse 성공: " + result?.toString()
                                                        );
                                                        // handle successful response
                                                        val intent =
                                                            Intent(
                                                                this@RegisterActivity,
                                                                FinishActivity::class.java
                                                            )
                                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                                        finish()
                                                    } else {
                                                        // handle error response
                                                    }
                                                }

                                                override fun onFailure(
                                                    call: Call<PostKakaoSigninResponse>,
                                                    t: Throwable
                                                ) {
                                                    // handle network or unexpected error
                                                }
                                            })
                                    }

                                }
                            } else {
                                // handle error response
                            }
                        }

                        override fun onFailure(call: Call<PostKakaoSignupResponse>, t: Throwable) {
                            // handle network or unexpected error
                        }
                    })

            }


        }
    }

    private fun selectButton(selectedButton: ImageButton, buttons: List<ImageButton>) {
        for (button in buttons) {
            if (button == selectedButton) {
                button.setColorFilter(Color.parseColor("#ffff0000"), PorterDuff.Mode.MULTIPLY);
            } else {
                button.setColorFilter(null)
            }
        }
    }
}
