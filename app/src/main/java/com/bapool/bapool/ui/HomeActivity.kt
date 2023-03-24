package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bapool.bapool.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        /************************************
         * 프론트 3차 통합 완료
         * 2023-03-24
         *
         * 연수)
        - 닉네임 화면 수정
        - 마이페이지 화면 추가 ; 레이팅 바(수정 필요)
        - 닉네임 변경 화면 추가
        - 로그아웃 기능 추가
         * 승현)
        - 식당의 그룹리스트 수정 ; 뫀서버 연결
        - 그룹생성화면 수정 ; 뫀서버 연결 ; 데이트/타임피커(수정 필요)
         * 현제)
        - 홈 화면을 그리기 위한 식당 정보 api 쿼리인자 변경 ; res_x ,y, radius -> rect
        - 맵 표시 관련 코드 함수화
        - 위치 권한 허용 시 맵 표시
        - 내 위치로 시작
        - 줌 레벨 19 제한
        - 사용자의 지도 이동 시 FAB 표시 / 첫 시작 시 및 버튼 터치 시 FAB 숨김
        - FAB -> ExtendedFAB
        - 마커 클릭 시 지도 이동
        - BottomSheetDialog(추가 필요)
        ---------------------------------------
         * Material Design 사용 권장 (extendedFAB, BottomSheetDialog)
        ---------------------------------------
         * 화면 분장
         * 현제)
        홈 - 검색/바텀시트2개
         * 승현)
        식당의그룹리스트/ 그룹생성
        네비게이션바(애니메이션)
         * 연수)
        회원탈퇴 - 팝업
        로그아웃 - 팝업
        차단관리 / 먹었던 정보
        레이팅 - 색깔 변경
         ************************************/
    }
}