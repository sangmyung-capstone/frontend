package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityHomeBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var navigationFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        val navController = navigationFragment.navController

        NavigationUI.setupWithNavController(binding.mainBottomNav, navController)


    }

    fun hideBottomNavi(state: Boolean) {
        if (state) binding.mainBottomNav.visibility = View.GONE
        else binding.mainBottomNav.visibility = View.VISIBLE
    }


}