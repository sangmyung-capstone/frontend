package com.bapool.bapool.ui

import android.content.Intent
import android.icu.number.Scale.none
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.bapool.bapool.R

class FinishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        Handler(Looper.getMainLooper()).postDelayed({
            val Intent = Intent(this, HomeActivity::class.java)
            startActivity(Intent)
            finish()
            //실행할 코드
        }, 1500)
    }
}