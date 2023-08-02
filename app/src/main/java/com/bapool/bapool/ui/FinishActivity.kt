package com.bapool.bapool.ui

import android.content.Intent
import android.icu.number.Scale.none
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityBlockListBinding
import com.bapool.bapool.databinding.ActivityFinishBinding
import com.bapool.bapool.databinding.ActivityLoginBinding



class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val nickname = intent.getStringExtra("nickname")
        binding.Finishtext.text = "$nickname" + "님 가입을 환영합니다."


        Handler(Looper.getMainLooper()).postDelayed({
            val Intent = Intent(this, HomeActivity::class.java)
            startActivity(Intent)
            finish()
            //실행할 코드
        }, 1500)
    }
}