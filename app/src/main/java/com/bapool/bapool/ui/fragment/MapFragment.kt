package com.bapool.bapool.ui.fragment

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.FragmentMapBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetRestaurantsResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var cameraPosition: CameraPosition
    private lateinit var rect: String
    val markerList: ArrayList<Marker> = arrayListOf() // 배열 생성

    val bapoolImg = OverlayImage.fromResource(R.drawable.bapool_circle) // by lazy
    val bapoolImgRed = OverlayImage.fromResource(R.drawable.bapool_circle_red)

    val retro = RetrofitService.create()  // MOCK SERVER
//    val retro = ServerRetrofit.create()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        // 위치 권한 요청
        requestPermissions()

        // 현 위치에서 검색 터치 시 // FAB
        binding.extendedFAB.setOnClickListener {

            markerInit()
            binding.extendedFAB.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .setDuration(400) // 0.4초로 변경
                .withEndAction {
                    binding.extendedFAB.hide()
                }
        }
        return binding.root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource // 실행된 네이버 지도에 locationSource 연결

        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 현 위치로 맵 시작

        naverMap.addOnCameraChangeListener { reason, _ ->
            // 지도가 이동 상태 시 콜백함수
            if (reason == -1) binding.extendedFAB.show()   // 사용자 움직임 시 reason == -1
        }

        naverMap.addOnCameraIdleListener {
            // 지도가 이동 후 대기 상태 시 콜백함수
            Log.d("MYTAG", "now camera : ${naverMap.cameraPosition}")
        }

//        markerInit()
        Handler(Looper.getMainLooper()).postDelayed({
            val cameraUpdate = CameraUpdate.zoomTo(16.0)
            naverMap.moveCamera(cameraUpdate)
            markerInit()
        }, 1500)
    }

    private fun mapInit() {


        // 초기 지도 옵션
        val options = NaverMapOptions()
//            .maxZoom(19.0)
            .compassEnabled(true)
            .zoomControlEnabled(false)
            .locationButtonEnabled(true)
//            .indoorEnabled(true) // 테스트 필요
//            .camera( // 현 위치 구하는 중...
//                CameraPosition(
//                    LatLng(37.4924505, 126.724422),
//                    19.0
//                )
//            )

        // Framelayout에 네이버 지도 띄우기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance(options).also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 현위치 반환 구현체 locationSource 선언
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun markerInit() {
        cameraPosition = naverMap.cameraPosition

        rect =
            "${naverMap.contentBounds.northWest.longitude},${naverMap.contentBounds.northWest.latitude},${naverMap.contentBounds.southEast.longitude},${naverMap.contentBounds.southEast.latitude}"

        Log.d("MYTAG", "rect : $rect")
        Log.d("MYTAG", "now bounds : ${naverMap.contentBounds}")
        Log.d("MYTAG", "now camera : $cameraPosition")
        //------------------------------------
        retro.getRestaurants(1, rect).enqueue(object : Callback<GetRestaurantsResult> {
            override fun onResponse(
                call: Call<GetRestaurantsResult>,
                response: Response<GetRestaurantsResult>
            ) {
                if (response.isSuccessful) {
                    // 기존 마커 존재 시 전부 삭제
                    if (markerList.size != 0) {
                        Log.d("MYTAG", "marker clear!!!")
                        for (i in 0 until markerList.size) markerList[i].map = null
                        markerList.clear()
                    }
                    // 마커 생성
                    for (i in 0 until response.body()!!.result.restaurants.size) { // 리스폰스로 받은 사이즈 만큼
                        markerList.add(i, Marker()) // 마커 할당
                        if (response.body()!!.result.restaurants[i].num_of_party != 0) // group이 있는 마커 표현
                            markerList[i].icon = bapoolImg
                        else
                            markerList[i].icon = bapoolImgRed
                        markerList[i].isHideCollidedSymbols = true
                        markerList[i].isHideCollidedCaptions = true
                        markerList[i].position = // 마커 위치 할당
                            LatLng(
                                response.body()!!.result.restaurants[i].restaurant_latitude,
                                response.body()!!.result.restaurants[i].restaurant_longitude
                            )
                        markerList[i].map = naverMap    // 생성 마커들 지도에 출력
                        markerList[i].width = 75
                        markerList[i].height = 75
                        markerList[i].captionText =
                            response.body()!!.result.restaurants[i].restaurant_name
                        markerList[i].setCaptionAligns(
                            Align.Bottom,
                            Align.Left,
                            Align.Right,
                            Align.Top,
                            Align.Center
                        )

                        // 해당 마커 클릭 이벤트
                        markerClickEvent(markerList[i])
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("MYTAG", "onResponse 실패")
                }
            }

            override fun onFailure(call: Call<GetRestaurantsResult>, t: Throwable) {
                Log.d("MYTAG", t.message.toString())
                Log.d("MYTAG", "FAIL")
            }
        })
    }

    private fun markerClickEvent(marker: Marker) {
        marker.setOnClickListener {
            // 해당 마커 위치로 지도 이동
            val cameraUpdate: CameraUpdate =
                CameraUpdate.scrollAndZoomTo(marker.position, 20.0)
            naverMap.moveCamera(cameraUpdate)

            // 마커 크기 변경 // 애니메이션 추가
            marker.width = 100
            marker.height = 100

            true
        }
    }

    // 위치 권한 획득 위함
    private fun requestPermissions() {
        // 권한 리스너 선언
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
                mapInit()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(
                    context,
                    "Permission Denied\n$deniedPermissions",
                    Toast.LENGTH_SHORT
                )
                    .show()
                /* 예외 처리 필요 */
            }
        }

        // TedPermission 라이브러리 // 위치 권한 요청
        TedPermission.create()
            .setPermissionListener(permissionlistener)
//            .setRationaleTitle("위치권한 요청")
//            .setRationaleMessage("현재 위치로 이동하기 위해 위치권한이 필요해요!!")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }


    //뷰바인딩 생명주기 관리
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // LOCATION_PERMISSION_REQUEST_CODE
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
}