package com.bapool.bapool.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationManagerCompat
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityHomeBinding
import com.bapool.bapool.receiver.MyReceiver.Companion.CHANNEL_ID
import com.bapool.bapool.receiver.MyReceiver.Companion.CHANNEL_NAME
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.fragment.MapFragment
import com.bapool.bapool.ui.fragment.MypageFragment
import com.bapool.bapool.ui.fragment.PartyFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var mapFragment: MapFragment? = null
    private var partyFragment: PartyFragment? = null
    private var mypageFragment: MypageFragment? = null
    private var waitTime = 0L


    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (System.currentTimeMillis() - waitTime >= 1500) {
                waitTime = System.currentTimeMillis()
                Toast.makeText(this@HomeActivity, "버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            } else {
                finishAffinity() // 종료
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        var navigationFragment =
//            supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment
//
//        val navController = navigationFragment.navController

        initBottomNavigation()
        refreshFirebaseToken()

        this.onBackPressedDispatcher.addCallback(this, callback) //위에서 생성한 콜백 인스턴스 붙여주기

        val destination = intent.getStringExtra("destination")
        if (destination == "MypageFragment") {
            binding.mainBottomNav.selectedItemId = R.id.mypageFragment
        } else if(destination == "PartyFragment"){
            binding.mainBottomNav.selectedItemId = R.id.partyFragment

        }



        createNotificationChannel()

        // Check if the notification permission is granted
        if (!isNotificationPermissionGranted()) {
            // If not granted, request permission
            requestNotificationPermission()
        }
    }

    private fun initBottomNavigation() {
        // 최초로 보이는 프래그먼트
//        mapFragment = MapFragment()
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragmentContainerView2, mapFragment!!).commit()


        binding.mainBottomNav.setOnItemSelectedListener {

            // 최초 선택 시 fragment add, 선택된 프래그먼트 show, 나머지 프래그먼트 hide
            when (it.itemId) {
                R.id.mapFragment -> {
                    // mapFragment의 경우 반드시 첫 실행되므로 아래 코드 주석처리함
//                    if (mapFragment == null) {
//                        mapFragment = MapFragment()
//                        supportFragmentManager.beginTransaction()
//                            .add(R.id.fragmentContainerView2, mapFragment!!).commit()
//                    }
                    if (mapFragment != null) supportFragmentManager.beginTransaction()
                        .show(mapFragment!!).commit()
                    if (partyFragment != null) supportFragmentManager.beginTransaction()
                        .hide(partyFragment!!).commit()
                    if (mypageFragment != null) supportFragmentManager.beginTransaction()
                        .hide(mypageFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.partyFragment -> {
                    if (partyFragment == null) {
                        partyFragment = PartyFragment()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragmentContainerView2, partyFragment!!).commit()
                    }
                    if (mapFragment != null) supportFragmentManager.beginTransaction()
                        .hide(mapFragment!!).commit()
                    if (partyFragment != null) supportFragmentManager.beginTransaction()
                        .show(partyFragment!!).commit()
                    if (mypageFragment != null) supportFragmentManager.beginTransaction()
                        .hide(mypageFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.mypageFragment -> {
                    if (mypageFragment == null) {
                        mypageFragment = MypageFragment()
                        supportFragmentManager.beginTransaction()
                            .add(R.id.fragmentContainerView2, mypageFragment!!).commit()
                    }
                    if (mapFragment != null) supportFragmentManager.beginTransaction()
                        .hide(mapFragment!!).commit()
                    if (partyFragment != null) supportFragmentManager.beginTransaction()
                        .hide(partyFragment!!).commit()
                    if (mypageFragment != null) supportFragmentManager.beginTransaction()
                        .show(mypageFragment!!).commit()

                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

//    @Deprecated("Deprecated in Java")
//    override fun onBackPressed() {
//        if (System.currentTimeMillis() - waitTime >= 1500) {
//            waitTime = System.currentTimeMillis()
//            Toast.makeText(this, "버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            finishAffinity() // 종료
//        }
//    }


    fun hideBottomNavi(state: Boolean) {
        if (state) binding.mainBottomNav.visibility = View.GONE
        else binding.mainBottomNav.visibility = View.VISIBLE
    }


    fun refreshFirebaseToken() {
        FirebaseDatabase.getInstance().getReference("test").child("Users").child(UserId.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val token = task.result

                                FirebaseDatabase.getInstance().getReference("test").child("Users")
                                    .child(UserId.toString()).child("firebaseToken")
                                    .setValue(token)
                            } else {
                                println("Failed to get FCM token: ${task.exception}")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "My Notification Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        // Check if the notification permission is granted
        return NotificationManagerCompat.from(this)
            .areNotificationsEnabled()
    }

    private fun requestNotificationPermission() {
        Toast.makeText(this,"알림권한 허용해줭",Toast.LENGTH_SHORT).show()
    }



}