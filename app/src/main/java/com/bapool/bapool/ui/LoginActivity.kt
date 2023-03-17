package com.bapool.bapool.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.databinding.ActivityLoginBinding
import com.bapool.bapool.kakaoUser
import com.bapool.bapool.retrofit.data.accessToken
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //카카오 초기화
        KakaoSdk.init(this, "b880bb2ce5431b600ab47061e4bc4c16")

        //네이버 초기화
        NaverIdLoginSDK.initialize(this, "CPpXcHZnKRPcbCvNR8i3", "PzO2ERMkZz", "bapool")
        //setContentView(R.layout.activity_login)
        //바인딩
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*
        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            } else if (tokenInfo != null) {
//                val intent = Intent(this, LogoutActivity::class.java)
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }


        val keyHash = Utility.getKeyHash(this)
        Log.d("Hash", keyHash)*/


        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(
                            this,
                            "설정이 올바르지 않음(android key hash)",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                //여기서부터 retrofit
                var token = token.toString()
                val retrofit =
                    Retrofit.Builder()
                        .baseUrl("https://655c8626-5f5d-4846-b60c-20c52d2ea0da.mock.pstmn.io")//baseurl
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val service = retrofit.create(kakaoUser::class.java)


                service.gettoken(token).enqueue(object : Callback<accessToken> {
                    override fun onResponse(
                        call: Call<accessToken>,
                        response: Response<accessToken>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            var result: accessToken? = response.body()
                            Log.d("bap", "onRequest 성공: $token")
                            Log.d("bap", "onResponse 성공: " + result?.toString())
                            if (result != null) {//처음 로그인시 가입화면으로 넘어감.
                                if (result.firstLogin) {
                                    val intent =
                                        Intent(this@LoginActivity, RegisterActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    finish()
                                } else {//처음 로그인 아니면 바로 홈 화면으로 넘어감
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "로그인에 성공하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    finish()
                                }
                            }
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("bap", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<accessToken>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("bap", "onFailure 에러: " + t.message.toString())
                    }
                })
                //여기까지 retrofit

            }
        }

        //네이버 로그인 코드
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                var token = NaverIdLoginSDK.getAccessToken()
                //여기서부터 retrofit
                val retrofit =
                    Retrofit.Builder()
                        .baseUrl("https://655c8626-5f5d-4846-b60c-20c52d2ea0da.mock.pstmn.io")//baseurl
                        .addConverterFactory(GsonConverterFactory.create()).build()
                val service = retrofit.create(kakaoUser::class.java)


                service.gettoken(token).enqueue(object : Callback<accessToken> {
                    override fun onResponse(
                        call: Call<accessToken>,
                        response: Response<accessToken>
                    ) {
                        if (response.isSuccessful) {
                            // 정상적으로 통신이 성공된 경우
                            var result: accessToken? = response.body()
                            Log.d("bap", "onRequest 성공: $token")
                            Log.d("bap", "onResponse 성공: " + result?.toString())
                            if (result != null) {//처음 로그인시 가입화면으로 넘어감.
                                if (result.firstLogin) {
                                    val intent =
                                        Intent(this@LoginActivity, RegisterActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    finish()
                                } else {//처음 로그인 아니면 바로 홈 화면으로 넘어감
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "로그인에 성공하였습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@LoginActivity, HomeActivity::class.java)
                                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                    finish()
                                }
                            }
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("bap", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<accessToken>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("bap", "onFailure 에러: " + t.message.toString())
                    }
                })
                //여기까지 retrofit

            }

            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(
                    this@LoginActivity,
                    "errorCode:$errorCode, errorDesc:$errorDescription",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        //카카오 로그인 버튼
        binding.kakaoLoginButton.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)


            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
        //네이버 로그인 버튼
        binding.naverLoginButton.setOnClickListener {
            NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
        }


    }
}