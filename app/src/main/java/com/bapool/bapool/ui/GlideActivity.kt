package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bapool.bapool.databinding.ActivityGlideBinding
import com.bapool.bapool.databinding.ActivityHomeBinding
import com.bumptech.glide.Glide

class GlideActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGlideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGlideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("GLIDE-TEST", "first")

        binding.button.setOnClickListener {
            Log.d("GLIDE-TEST", "click")
            Glide.with(this)
                .load("https://t1.kakaocdn.net/thumb/T800x0.q80/?fname=http%3A%2F%2Ft1.daumcdn.net%2Fplace%2F4274A699F61A4D07BEF31567CBC14323")
                .into(binding.imageView6)
            Log.d("GLIDE-TEST", "end")
        }
    }
}