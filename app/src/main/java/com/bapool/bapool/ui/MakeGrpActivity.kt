package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityMakeGrpBinding


class MakeGrpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeGrpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeGrpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var resName = intent.getStringExtra("resName")
        binding.resName.setText(resName)


//        initializeVari()
        listener()

    }

    //
//    fun initializeVari() {
//
//    }
//
    fun listener() {
        val image = binding.hash5
        image.setOnClickListener {
            val currentState = image.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image.setBackgroundResource(R.drawable.custom_img_bg_pressed)
            } else {
                image.setBackgroundResource(R.drawable.custom_img_bg)
            }
        }
    }
//    }
}