package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityChattingAndPartyInfoBinding
import com.bapool.bapool.retrofit.data.FirebaseGroup
import com.bapool.bapool.retrofit.data.FirebaseGroups
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChattingAndPartyInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChattingAndPartyInfoBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingAndPartyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        listener()

    }


    fun listener() {
        binding.cancelButton.setOnClickListener {
            Toast.makeText(this, "취소버튼", Toast.LENGTH_SHORT).show()
            val database = Firebase.database
            val myRef = database.getReference("message")

            myRef.push().setValue("Hello, World!")


        }
    }


}