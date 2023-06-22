package com.bapool.bapool.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.databinding.ActivityLoginBinding
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApi
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    companion object {
        var UserToken: String? = null
        var UserId: Long? = null
    }

    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    //    val retro = RetrofitService.create()    // Mock Server
    val retro = ServerRetrofit.create()

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
                //여기서부터 retrofit
                Log.d("bap", "토큰 정보 : ${token.accessToken}")
                UserToken = token.accessToken

                //여기까지 retrofit
                retro.PostKakaoLoginCheck(PostkakaoLoginCheckRequest(token.accessToken))
                    .enqueue(object : Callback<PostKakaoLoginCheckResponse> {
                        override fun onResponse(
                            call: Call<PostKakaoLoginCheckResponse>,
                            response: Response<PostKakaoLoginCheckResponse>
                        ) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우
                                var result: PostKakaoLoginCheckResponse? = response.body()
                                Log.d("bap", "onRequest 성공: $UserToken")
                                Log.d("bap", "onResponse 성공: " + result?.toString())
                                if (result != null) {//처음 로그인시 가입화면으로 넘어감.
                                    if (!result.result) {
                                        val intent =
                                            Intent(this@LoginActivity, RegisterActivity::class.java)
                                        intent.putExtra(
                                            "token",
                                            token.accessToken
                                        )
                                        intent.putExtra("company", "kakao")
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                        finish()
                                    } else {//처음 로그인 아니면 바로 홈 화면으로 넘어감
                                        retro.PostKakaoSingin(PostkakaoSigninRequest(token.accessToken))
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
                                                        }
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "로그인 성공",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        Log.d("bap", "onRequest 성공: $token");
                                                        Log.d(
                                                            "bap",
                                                            "onResponse 성공: " + result?.toString()
                                                        );
                                                        // handle successful response
                                                        val intent =
                                                            Intent(
                                                                this@LoginActivity,
                                                                HomeActivity::class.java
                                                            )
                                                        intent.putExtra("token", token.accessToken)
                                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                        finish()
                                    }
                                }
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d("bap", "onResponse 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostKakaoLoginCheckResponse>,
                            t: Throwable
                        ) {
                            // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                            Log.d("bap", "onFailure 에러: " + t.message.toString())
                        }
                    })

            }
        }

        //네이버 로그인 코드
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                var token = PostNaverLoginCheckRequest(NaverIdLoginSDK.getAccessToken().toString())
                Log.d("bap", "OnRequest : $token")
                UserToken = token.access_token

                //여기서부터 retrofit
                retro.PostNaverLoginCheck(token)
                    .enqueue(object : Callback<PostNaverLoginCheckResponse> {
                        override fun onResponse(
                            call: Call<PostNaverLoginCheckResponse>,
                            response: Response<PostNaverLoginCheckResponse>
                        ) {
                            if (response.isSuccessful) {
                                // 정상적으로 통신이 성공된 경우
                                var result: PostNaverLoginCheckResponse? = response.body()
                                Log.d("bap", "onRequest 성공: $token")
                                Log.d("bap", "onResponse 성공: " + result?.toString())
                                if (result != null) {//처음 로그인시 가입화면으로 넘어감.
                                    if (!result.result) {//result가 true가 아니면
                                        val intent =
                                            Intent(this@LoginActivity, RegisterActivity::class.java)
                                        intent.putExtra(
                                            "token",
                                            NaverIdLoginSDK.getAccessToken().toString()
                                        )
                                        intent.putExtra("company", "naver")
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                        finish()
                                    } else {//처음 로그인 아니면 바로 홈 화면으로 넘어감
                                        retro.PostNaverSingin(
                                            PostNaverSigninRequest(
                                                NaverIdLoginSDK.getAccessToken().toString()
                                            )
                                        )
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
                                                        }
                                                        Toast.makeText(
                                                            this@LoginActivity,
                                                            "로그인 성공",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        Log.d("bap", "onRequest 성공: $token");
                                                        Log.d(
                                                            "bap",
                                                            "onResponse 성공: " + result?.toString()
                                                        );
                                                        // handle successful response
                                                        val intent =
                                                            Intent(
                                                                this@LoginActivity,
                                                                HomeActivity::class.java
                                                            )
                                                        intent.putExtra("token", "$token")
                                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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
                                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                                        finish()
                                    }
                                }
                            } else {
                                // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                Log.d("bap", "onResponse 실패")
                            }
                        }

                        override fun onFailure(
                            call: Call<PostNaverLoginCheckResponse>,
                            t: Throwable
                        ) {
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