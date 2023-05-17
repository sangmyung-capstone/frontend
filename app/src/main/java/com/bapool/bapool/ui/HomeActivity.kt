package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var navigationFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView2) as NavHostFragment

        val navController = navigationFragment.navController

        NavigationUI.setupWithNavController(binding.mainBottomNav, navController)


        /************************************
         * 전체 1차 통합
         * 2023-03-31
         *
         * 연수)
        - 네이버 로그인 백엔드와 연결
        - 카카오 로그인 백엔드와 연결?
        - 차단 리스트 화면
        - 먹은 기록 리스트 화면
         * 승현)
        - 식당의 그룹리스트 수정 ; 뫀서버 연결
        - 그룹생성화면 수정 ; 데이트/타임피커 관련 이슈 해결 및 예외처리 완료
         * 현제)
        - 지도 화면 백엔드와 연결 완료
        ---------------------------------------
        ---------------------------------------
         * 화면 분장

        - 중간고사 이후까지 업무 일시정지
         ************************************/
    }
}