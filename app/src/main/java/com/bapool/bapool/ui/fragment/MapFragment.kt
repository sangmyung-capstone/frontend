package com.bapool.bapool.ui.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.bapool.bapool.R
import com.bapool.bapool.databinding.FragmentMapBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetRestaurantInfoResult
import com.bapool.bapool.retrofit.data.GetRestaurantsResult
import com.bapool.bapool.retrofit.data.RestaurantInfo
import com.bapool.bapool.ui.HomeActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Align
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
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

    //    val retro = RetrofitService.create()  // MOCK SERVER
    val retro = ServerRetrofit.create()

    var cnt = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)


        // 위치 권한 요청
        requestPermissions()

        // bottomSheet에 식당바텀리스트 레이아웃 할당
        layoutInflater.inflate(R.layout.bottom_restaurant_list, binding.bottomSheet, true)

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

        val bottomBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        // behavior 속성
        bottomBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomBehavior.peekHeight = dpToPx(180f, context).toInt()    // dp -> px변환
//        bottomBehavior.isFitToContents = false
//        bottomBehavior.expandedOffset = dpToPx(250f, context).toInt()    // dp -> px변환


        bottomBehavior.apply {
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_COLLAPSED -> { //접힘
//                            binding.bottomSheet.findViewById<TextView>(R.id.bottomTextPartyList).text =
//                                "OPEN"
                        }
                        BottomSheetBehavior.STATE_EXPANDED -> {  //펼쳐짐
//                            binding.bottomSheet.findViewById<TextView>(R.id.bottomTextPartyList).text =
//                                "CLOSE"
                        }
                        BottomSheetBehavior.STATE_HIDDEN -> {}    //숨겨짐
                        BottomSheetBehavior.STATE_HALF_EXPANDED -> {} //절반 펼쳐짐
                        BottomSheetBehavior.STATE_DRAGGING -> {}  //드래그하는 중
                        BottomSheetBehavior.STATE_SETTLING -> {}  //(움직이다가) 안정화되는 중
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    //슬라이드 될때 offset / hide -1.0 ~ collapsed 0.0 ~ expended 1.0
                    // 커스텀 위젯의 마진 동기화 작업 중...
                    val layoutParams = binding.logoView.layoutParams as ViewGroup.MarginLayoutParams
                    layoutParams.bottomMargin += (binding.bottomSheet.height * slideOffset).toInt()
                    binding.logoView.layoutParams = layoutParams
                }
            })
        }

        binding.bottomSheet.setOnClickListener {
            bottomBehavior.state =
                if (bottomBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    BottomSheetBehavior.STATE_EXPANDED
                } else
                    BottomSheetBehavior.STATE_COLLAPSED
        }


        return binding.root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource // 실행된 네이버 지도에 locationSource 연결
        binding.locationView.map = naverMap // 커스텀 위치 위젯 맵에 바인딩
        binding.scaleView.map = naverMap    // 커스텀 스케일 바 위젯
        binding.logoView.setMap(naverMap) // 커스텀 로고 위젯

        naverMap.locationTrackingMode = LocationTrackingMode.Follow // 현 위치로 맵 시작

        naverMap.addOnCameraChangeListener { reason, _ ->
            // 지도가 이동 상태 시 콜백함수
            if (reason == -1) binding.extendedFAB.show()   // 사용자 움직임 시 reason == -1
        }

        naverMap.addOnCameraIdleListener {
            // 지도가 이동 후 대기 상태 시 콜백함수
            Log.d("CAMERA", "now camera : ${naverMap.cameraPosition}")
        }

//        markerInit()  // 현 위치 구하기 필요
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
            .minZoom(4.5)
            .logoMargin(0, 0, 0, -50)   // 기본 네이버 로고 숨기기
            .compassEnabled(true)
            .scaleBarEnabled(false)
            .zoomControlEnabled(false)
            .locationButtonEnabled(false)
            .extent(LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0)))  // 한반도 인근으로 설정
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

        Log.d("MARKER_INIT", "rect : $rect")
        Log.d("MARKER_INIT", "now bounds : ${naverMap.contentBounds}")
        Log.d("MARKER_INIT", "now camera : $cameraPosition")
        //------------------------------------
        retro.getRestaurants(1, rect).enqueue(object : Callback<GetRestaurantsResult> {
            override fun onResponse(
                call: Call<GetRestaurantsResult>,
                response: Response<GetRestaurantsResult>
            ) {
                if (response.isSuccessful) {
                    // 기존 마커 존재 시 전부 삭제
                    if (markerList.size != 0) {
                        Log.d("MARKER_INIT", "marker clear!!!")
                        for (i in 0 until markerList.size) markerList[i].map = null
                        markerList.clear()
                    }
                    Log.d("MARKER_INIT", response.body()!!.result.toString())
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
                        markerClickEvent(
                            markerList[i],
                            response.body()!!.result.restaurants[i].restaurant_id,
                            response.body()!!.result.restaurants[i].restaurant_longitude,
                            response.body()!!.result.restaurants[i].restaurant_latitude
                        )
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("MARKER_INIT", "onResponse 실패")
                    Log.d("MARKER_INIT", response.body().toString())
                }
            }

            override fun onFailure(call: Call<GetRestaurantsResult>, t: Throwable) {
                Log.d("MARKER_INIT", t.message.toString())
                Log.d("MARKER_INIT", "FAIL")
            }
        })
    }

    private fun markerClickEvent(marker: Marker, id: Long, long: Double, lati: Double) {
        marker.setOnClickListener {

            Log.d("MARKER_INFO", "lati: $lati long: $long")
            Log.d("MARKER_INFO", "Restaurant id : $id")

            binding.imageView4.visibility = View.VISIBLE
            Glide.with(this)
                .load("https://t1.kakaocdn.net/thumb/T800x0.q80/?fname=http%3A%2F%2Ft1.daumcdn.net%2Fplace%2F4274A699F61A4D07BEF31567CBC14323")
                .into(binding.imageView4.findViewById(R.id.imageView4))

            Log.d("MARKER_INFO", "GLIDE FINISH")


            // 마커 클릭 시 하단 네비게이션바 제거
            val homeActivity = activity as HomeActivity
            homeActivity.hideBottomNavi(true)

            if (cnt == 0) {     // cnt 방식이 아닌 뷰 교체 방식의 제대로된 방법 필요
                layoutInflater.inflate(R.layout.bottom_marker_info, binding.bottomSheet, true)
                cnt++
            }


            retro.getRestaurantInfo(1, id, long, lati)
                .enqueue(object : Callback<GetRestaurantInfoResult> {
                    override fun onResponse(
                        call: Call<GetRestaurantInfoResult>,
                        response: Response<GetRestaurantInfoResult>
                    ) {
                        if (response.isSuccessful) {
                            Log.d("MARKER_INFO", response.body().toString())
                            createMarkerInfo(response.body()?.result)
                        } else {
                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                            Log.d("MARKER_INFO", "onResponse 실패")
                        }
                    }

                    override fun onFailure(call: Call<GetRestaurantInfoResult>, t: Throwable) {
                        Log.d("MARKER_INFO", t.message.toString())
                        Log.d("MARKER_INFO", "FAIL")
                    }
                })


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

    fun createMarkerInfo(result: RestaurantInfo?) {
        Log.d("MARKER_INFO", "img_url : ${result?.img_url}")
        // 이미지
        Glide.with(this)
            .load("https:" + result?.img_url)
            .placeholder(R.drawable.hashtag1) // Glide 로 이미지 로딩을 시작하기 전에 보여줄 이미지를 설정한다.
            .error(R.drawable.hashtag5) // error 시 보여줄 이미지
            .into(binding.bottomSheet.findViewById<ImageView>(R.id.bottomImageMarkerInfo))
        // 스트링
        binding.bottomSheet.findViewById<TextView>(R.id.bottomTextName).text =
            result?.restaurant_name
        binding.bottomSheet.findViewById<TextView>(R.id.bottomTextAddress).text =
            result?.restaurant_address
        binding.bottomSheet.findViewById<TextView>(R.id.bottomTextPhone).text =
            result?.phone
        binding.bottomSheet.findViewById<TextView>(R.id.bottomTextCategory).text =
            result?.category
        if (result?.menu != null) {
            binding.bottomSheet.findViewById<TextView>(R.id.bottomTextMenu).text =
                    // 사이즈 측정해서 for문 필요
                    // resource string 필요!!!
                "${result.menu[0].name} : ${result.menu[0].price}"
        } else {
            binding.bottomSheet.findViewById<TextView>(R.id.bottomTextMenu).text =
                "메뉴 미제공"
        }
        binding.bottomSheet.findViewById<Button>(R.id.bottomButtonParty).text =
            "${result?.num_of_party.toString()} 파티!"
        // view 부착
//        binding.bottomSheet.addView(BottomMarkerInfoBinding.inflate(layoutInflater).root)
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

        val homeActivity = activity as HomeActivity
        homeActivity.hideBottomNavi(false)
    }

    // LOCATION_PERMISSION_REQUEST_CODE
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    fun dpToPx(dp: Float, context: Context?): Float {
        val displayMetrics = context?.resources?.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
    }
}