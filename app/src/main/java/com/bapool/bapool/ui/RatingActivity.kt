package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bapool.bapool.adapter.BlockListAdapter
import com.bapool.bapool.databinding.ActivityBlockListBinding
import com.bapool.bapool.databinding.ActivityLoginBinding
import com.bapool.bapool.databinding.ActivityRatinguserBinding

private var _binding: ActivityRatinguserBinding? = null
private val binding get() = _binding!!

class RatingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRatinguserBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}