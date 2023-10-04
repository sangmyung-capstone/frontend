package com.bapool.bapool.ui.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.transition.Transition
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.annotation.UiThread
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.R
import com.bapool.bapool.adapter.RestaurantBottomAdapter
import com.bapool.bapool.adapter.RestaurantBottomListAdapter
import com.bapool.bapool.adapter.SearchViewAdapter
import com.bapool.bapool.databinding.FragmentMapBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.RestaurantPartyActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.search.SearchView
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
import java.io.File
import java.util.*


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var cameraPosition: CameraPosition
    private lateinit var rect: String

    companion object {
        // LOCATION_PERMISSION_REQUEST_CODE
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

        val markerList: ArrayList<Marker> = arrayListOf() // 배열 생성

        private var instance: com.bapool.bapool.ui.fragment.MapFragment? = null
        fun getInstance(): com.bapool.bapool.ui.fragment.MapFragment? {
            return instance
        }
    }

    val markerImage = OverlayImage.fromResource(R.drawable.marker_icon)
    val markerImageEmpty = OverlayImage.fromResource(R.drawable.marker_icon_empty)
    val markerPinImage = OverlayImage.fromResource(R.drawable.marker_pin)
    val markerPinImageEmpty = OverlayImage.fromResource(R.drawable.marker_pin_empty)

    val markerSize = 70
    val markerPinSize = 160


    //    val retro = RetrofitService.create()  // MOCK SERVER
    val retro = ServerRetrofit.create()

    var restaurantsList = mutableListOf<Restaurant>()  // restaurant 리스트 생성
    var restaurantIdList = mutableListOf<Long>()    // id 리스트 생성
    var restaurantImageList = mutableListOf<String>()  // image url 리스트 생성

    private lateinit var adapter: RestaurantBottomListAdapter

    var waitTime = 0L

    init {
        instance = this
    }

    private var lastMarker: Marker? = null
    private var searchGoFlag = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)


        // 위치 권한 요청 및 맵 초기화
        requestPermissions()

        // search bar
        binding.searchView.editText.setOnEditorActionListener { v, i, keyEvent ->
            Log.d("search", v.text.toString())
            Log.d("search_tagert", cameraPosition.target.toString())

            retro.getRestaurantsSearch(
                UserId,
                v.text.toString(),
                cameraPosition.target.longitude,
                cameraPosition.target.latitude
            ).enqueue(object : Callback<GetSearchResult> {
                override fun onResponse(
                    call: Call<GetSearchResult>,
                    response: Response<GetSearchResult>,
                ) {
                    Log.d("search", "response : ${response.toString()}")
                    // 식당검색결과 recyclerview 어댑터 바인딩
                    binding.searchRecyclerView.adapter =
                        SearchViewAdapter(response.body()!!.result.restaurants, naverMap)
                    binding.searchRecyclerView.layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }

                override fun onFailure(call: Call<GetSearchResult>, t: Throwable) {
                    Log.d("search", t.message.toString())
                    Log.d("search", "FAIL")
                }
            })


            false
        }

        val searchViewCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.searchView.hide()
            }
        }

        binding.searchView.addTransitionListener { searchView, previousState, newState ->
            if (newState == SearchView.TransitionState.SHOWING) {
                requireActivity().onBackPressedDispatcher.addCallback(searchViewCallback)
            }
            else if (newState == SearchView.TransitionState.HIDDEN) {
                searchViewCallback.remove()
            }
        }

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
        naverMap.setContentPadding(50, 300, 50, 600)    // 지도 padding

        binding.locationView.map = naverMap // 커스텀 위치 위젯 맵에 바인딩
        binding.scaleView.map = naverMap    // 커스텀 스케일 바 위젯
        binding.logoView.setMap(naverMap) // 커스텀 로고 위젯
        binding.compassView.map = naverMap


        naverMap.addOnCameraChangeListener { reason, _ ->
            // 지도가 이동 상태 시 콜백함수
            if (reason == -1) binding.extendedFAB.show()   // 사용자 움직임 시 reason == -1


        }

        naverMap.addOnCameraIdleListener {
            // 지도가 이동 후 대기 상태 시 콜백함수
            Log.d("CAMERA", "now camera : ${naverMap.cameraPosition}")
            Log.d("CAMERA", "bottomRestaurant : ${binding.bottomRestaurantList.visibility}")
            Log.d("CAMERA", "bottomMarker : ${binding.bottomMarkerInfo.visibility}")

        }

        Handler(Looper.getMainLooper()).postDelayed({
            val cameraUpdate = CameraUpdate.zoomTo(16.0)
            naverMap.moveCamera(cameraUpdate)
            markerInit()
        }, 3000)
    }

    private fun mapInit() {

        // 초기 지도 옵션
        val options = NaverMapOptions()
//            .maxZoom(19.0)
//            .symbolScale(0f)  // 심벌 숨기기
            .minZoom(4.5)
            .logoMargin(0, 0, 0, -50)   // 기본 네이버 로고 숨기기
            .compassEnabled(true)
            .scaleBarEnabled(false)
            .zoomControlEnabled(false)
            .locationButtonEnabled(false)
            .extent(LatLngBounds(LatLng(31.43, 122.37), LatLng(44.35, 132.0)))  // 한반도 인근으로 설정
//            .indoorEnabled(true) // 실내 지도

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

    // 첫 실행 및 현위치에서 재검색 시
    // 최대 45개 마커 생성 및 식당바텀리스트 생성
    private fun markerInit() {
        val loggedPositions = HashSet<Int>()

        cameraPosition = naverMap.cameraPosition
        restaurantImageList = MutableList(45) { "a" }

        rect =
            "${naverMap.contentBounds.northWest.longitude},${naverMap.contentBounds.northWest.latitude},${naverMap.contentBounds.southEast.longitude},${naverMap.contentBounds.southEast.latitude}"

        Log.d("MARKER_INIT", "rect : $rect")
        Log.d("MARKER_INIT", "now bounds : ${naverMap.contentBounds}")
        Log.d("MARKER_INIT", "now camera : $cameraPosition")

        binding.bottomRestaurantList.removeAllViews()

        binding.bottomRestaurantList.visibility = View.VISIBLE
        binding.bottomMarkerInfo.visibility = View.GONE

        //------------------------------------
        retro.getRestaurants(UserId, rect).enqueue(object : Callback<GetRestaurantsResult> {
            override fun onResponse(
                call: Call<GetRestaurantsResult>,
                response: Response<GetRestaurantsResult>,
            ) {
                if (response.isSuccessful) {
                    restaurantsList = response.body()!!.result.restaurants.toMutableList()

                    // 식당링크 모음  // 현위치에서 재검색 이후 리스트 사이즈만큼 api 요청 필요 및 바인딩
                    restaurantIdList.clear()
                    for (idx in response.body()!!.result.restaurants)
                        restaurantIdList.add(idx.restaurant_id)

                    Log.d("BOTTOM_ID_LIST", restaurantIdList.toString())

                    // bottomSheet에 식당바텀리스트 레이아웃 할당
                    layoutInflater.inflate(
                        R.layout.bottom_restaurant_list,
                        binding.bottomRestaurantList,
                        true
                    )
                    BottomSheetBehavior.from(binding.bottomRestaurantList).isDraggable = true
                    BottomSheetBehavior.from(binding.bottomRestaurantList).peekHeight =
                        dpToPx(190f, context).toInt()


                    // 식당바텀리스트화면에 리스트 연결 (리사이클러뷰)
                    adapter = RestaurantBottomListAdapter(
                        response.body()!!.result.restaurants,
                        restaurantImageList,
                        naverMap
                    )

                    binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview).adapter =
                        adapter
                    binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview).layoutManager =
                        LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                    binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview)
                        .apply {
                            itemAnimator = null
                        }

                    adapter.submitList(restaurantImageList.toList())


                    // 식당바텀리스트 통신 1차
                    Log.d("BOTTOM_ID_SIZE", restaurantIdList.size.toString())
                    Log.d("BOTTOM_RESTAURANTS", restaurantsList.toString())

                    // 식당이 5개 미만일 경우
                    if (restaurantIdList.size < 5) {
                        retro.getRestaurantsBottom(
                            UserId,
                            GetRestaurantsBottomRequest(
                                restaurantIdList
                            )
                        )
                            .enqueue(object : Callback<GetRestaurantsBottomResult> {
                                override fun onResponse(
                                    call: Call<GetRestaurantsBottomResult>,
                                    response: Response<GetRestaurantsBottomResult>
                                ) {
                                    if (response.isSuccessful) {
                                        for (idx in 0 until restaurantIdList.size)
                                            restaurantImageList[idx] =
                                                response.body()!!.result.restaurant_img_urls[idx]

                                        adapter.submitList(restaurantImageList.toList())

                                    }
                                }

                                override fun onFailure(
                                    call: Call<GetRestaurantsBottomResult>,
                                    t: Throwable
                                ) {
                                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                    Log.d("BOTTOM", "onResponse 실패")
                                    Log.d("BOTTOM", response.body().toString())
                                }

                            })

                    }

                    // 식당이 5개 이상일 경우
                    else {
                        retro.getRestaurantsBottom(
                            UserId,
                            GetRestaurantsBottomRequest(
                                listOf(
                                    restaurantIdList[0],
                                    restaurantIdList[1],
                                    restaurantIdList[2],
                                    restaurantIdList[3],
                                    restaurantIdList[4]
                                )
                            )
                        )
                            .enqueue(object : Callback<GetRestaurantsBottomResult> {
                                override fun onResponse(
                                    call: Call<GetRestaurantsBottomResult>,
                                    response: Response<GetRestaurantsBottomResult>,
                                ) {
                                    if (response.isSuccessful) {
                                        // 5개 호출이므로 0, 1, 2, 3, 4
                                        restaurantImageList[0] =
                                            response.body()!!.result.restaurant_img_urls[0]
                                        restaurantImageList[1] =
                                            response.body()!!.result.restaurant_img_urls[1]
                                        restaurantImageList[2] =
                                            response.body()!!.result.restaurant_img_urls[2]
                                        restaurantImageList[3] =
                                            response.body()!!.result.restaurant_img_urls[3]
                                        restaurantImageList[4] =
                                            response.body()!!.result.restaurant_img_urls[4]

                                        adapter.submitList(restaurantImageList.toList())

                                    }
                                }

                                override fun onFailure(
                                    call: Call<GetRestaurantsBottomResult>,
                                    t: Throwable,
                                ) {
                                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                    Log.d("BOTTOM", "onResponse 실패")
                                    Log.d("BOTTOM", response.body().toString())
                                }

                            })

                        // 스크롤 페이징 리스너
                        binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview)
                            .addOnScrollListener(object : RecyclerView.OnScrollListener() {
                                override fun onScrolled(
                                    recyclerView: RecyclerView,
                                    dx: Int,
                                    dy: Int
                                ) {
                                    super.onScrolled(recyclerView, dx, dy)

                                    val firstVisibleItemPosition =
                                        (binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview).layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

                                    // 3 간격 아이템 '진입 시'
                                    if ((firstVisibleItemPosition + 1) % 3 == 0 && !loggedPositions.contains(
                                            firstVisibleItemPosition
                                        )
                                    ) {
                                        loggedPositions.add(firstVisibleItemPosition)
                                        Log.d(
                                            "paging",
                                            "Scrolled to position $firstVisibleItemPosition"
                                        )
                                        Log.d(
                                            "paging",
                                            "my page ${(firstVisibleItemPosition + 1) / 3 * 5}"
                                        )

                                        val cnt = (firstVisibleItemPosition + 1) / 3 * 5

                                        // 식당바텀리스트 통신 2차
                                        if (cnt < restaurantIdList.size) {

                                            Log.d(
                                                "BOTTOM_ID_SIZE",
                                                restaurantIdList.size.toString()
                                            )
                                            Log.d("BOTTOM_RESTAURANTS", restaurantsList.toString())

                                            if (restaurantIdList.size - cnt < 5) {  // 마지막 페이징 시 // size가 17 24 등의 경우

                                                var lastRestaurantIdList = mutableListOf<Long>()
                                                for (idx in 0 until restaurantIdList.size - cnt)
                                                    lastRestaurantIdList.add(restaurantIdList[cnt + idx])

                                                retro.getRestaurantsBottom(
                                                    UserId,
                                                    GetRestaurantsBottomRequest(lastRestaurantIdList)
                                                )
                                                    .enqueue(object :
                                                        Callback<GetRestaurantsBottomResult> {
                                                        override fun onResponse(
                                                            call: Call<GetRestaurantsBottomResult>,
                                                            response: Response<GetRestaurantsBottomResult>,
                                                        ) {
                                                            if (response.isSuccessful) {

                                                                for (idx in 0 until restaurantIdList.size - cnt)
                                                                    restaurantImageList[cnt + idx] =
                                                                        response.body()!!.result.restaurant_img_urls[idx]

                                                                adapter.submitList(
                                                                    restaurantImageList.toList()
                                                                )

                                                                Log.d(
                                                                    "RESTAURANT_IMG",
                                                                    restaurantImageList.toString()
                                                                )

                                                            }

                                                        }

                                                        override fun onFailure(
                                                            call: Call<GetRestaurantsBottomResult>,
                                                            t: Throwable,
                                                        ) {
                                                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                                            Log.d("BOTTOM", "onResponse 실패")
                                                            Log.d(
                                                                "BOTTOM",
                                                                response.body().toString()
                                                            )
                                                        }
                                                    })
                                            } else {  // 5개 통신
                                                retro.getRestaurantsBottom(
                                                    UserId,
                                                    GetRestaurantsBottomRequest(
                                                        listOf(
                                                            restaurantIdList[cnt + 0],
                                                            restaurantIdList[cnt + 1],
                                                            restaurantIdList[cnt + 2],
                                                            restaurantIdList[cnt + 3],
                                                            restaurantIdList[cnt + 4]
                                                        )
                                                    )
                                                )
                                                    .enqueue(object :
                                                        Callback<GetRestaurantsBottomResult> {
                                                        override fun onResponse(
                                                            call: Call<GetRestaurantsBottomResult>,
                                                            response: Response<GetRestaurantsBottomResult>,
                                                        ) {
                                                            if (response.isSuccessful) {

                                                                // 5개 호출이므로 0, 1, 2, 3, 4
                                                                restaurantImageList[cnt + 0] =
                                                                    response.body()!!.result.restaurant_img_urls[0]
                                                                restaurantImageList[cnt + 1] =
                                                                    response.body()!!.result.restaurant_img_urls[1]
                                                                restaurantImageList[cnt + 2] =
                                                                    response.body()!!.result.restaurant_img_urls[2]
                                                                restaurantImageList[cnt + 3] =
                                                                    response.body()!!.result.restaurant_img_urls[3]
                                                                restaurantImageList[cnt + 4] =
                                                                    response.body()!!.result.restaurant_img_urls[4]


                                                                adapter.submitList(
                                                                    restaurantImageList.toList()
                                                                )


                                                                Log.d(
                                                                    "RESTAURANT_IMG",
                                                                    restaurantImageList.toString()
                                                                )

                                                            }

                                                        }

                                                        override fun onFailure(
                                                            call: Call<GetRestaurantsBottomResult>,
                                                            t: Throwable,
                                                        ) {
                                                            // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                                                            Log.d("BOTTOM", "onResponse 실패")
                                                            Log.d(
                                                                "BOTTOM",
                                                                response.body().toString()
                                                            )
                                                        }
                                                    })
                                            }

                                        } else {
                                            Log.d("PAGING END", "cnt : $cnt !! PAGING END !!")
                                        }



                                        if (cnt <= 40) {    // cnt = 5, 10, 15 ~


                                        }

                                    }

                                    // 최하단
                                    if (!binding.bottomRestaurantList.findViewById<RecyclerView>(R.id.bottom_recyclerview)
                                            .canScrollVertically(1)
                                    ) {   //최하단에 오면
                                        //원하는 동작
                                        Log.d("paging", "bottom?")
                                    }

                                }
                            })

                    }


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
                        if (response.body()!!.result.restaurants[i].num_of_party != 0)  // 파티가 있는 마커 표현
                            markerList[i].icon = markerImage
                        else
                            markerList[i].icon = markerImageEmpty
                        markerList[i].isHideCollidedSymbols = true
                        markerList[i].isHideCollidedCaptions = true
                        markerList[i].position = // 마커 위치 할당
                            LatLng(
                                response.body()!!.result.restaurants[i].restaurant_latitude,
                                response.body()!!.result.restaurants[i].restaurant_longitude
                            )
                        markerList[i].map = naverMap    // 생성 마커들 지도에 출력
                        markerList[i].width = markerSize
                        markerList[i].height = markerSize
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

    // 식당바텀리스트에서 식당 클릭 시 식당정보화면 이동
    fun markerGoEvent(naverMap: NaverMap, marker: Marker, id: Long, long: Double, lati: Double) {
        Log.d("MARKER_INFO", "lati: $lati long: $long")
        Log.d("MARKER_INFO", "Restaurant id : $id")


        // 해당 마커 위치로 지도 이동
        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(marker.position, 20.0))

        if (marker.icon == markerImage) {

            marker.icon = markerPinImage
            ObjectAnimator.ofInt(marker, "width", markerPinSize).start()
            ObjectAnimator.ofInt(marker, "height", markerPinSize).start()

        } else if (marker.icon == markerImageEmpty) {

            marker.icon = markerPinImageEmpty
            ObjectAnimator.ofInt(marker, "width", markerPinSize).start()
            ObjectAnimator.ofInt(marker, "height", markerPinSize).start()

        }

        retro.getRestaurantInfo(UserId, id, long, lati)
            .enqueue(object : Callback<GetRestaurantInfoResult> {
                override fun onResponse(
                    call: Call<GetRestaurantInfoResult>,
                    response: Response<GetRestaurantInfoResult>,
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

        lastMarker = marker
    }

    // 검색결과에서 식당 클릭 시 식당정보화면 이동
    fun searchMarkerGoEvent(
        naverMap: NaverMap,
        id: Long,
        long: Double,
        lati: Double,
        isParty: Boolean,
        restaurant_name: String
    ) { // 인자로서 marker의 lati long 필요  // num of party   // name
        binding.searchView.hide()

        searchGoFlag = 1

        // 추가적인 마커 생성 및 카메라 이동 필요
        if (markerList.size != 0) {
            Log.d("MARKER_INIT", "marker clear!!!")
            for (i in 0 until markerList.size) markerList[i].map = null
            markerList.clear()
        }
        markerList.add(0, Marker()) // 마커 할당
        if (isParty)  // 파티가 있는 마커 표현
            markerList[0].icon = markerImage
        else
            markerList[0].icon = markerImageEmpty
        markerList[0].isHideCollidedSymbols = true
        markerList[0].isHideCollidedCaptions = true
        markerList[0].position = LatLng(lati, long)  // 마커 위치 정보
        markerList[0].map = naverMap    // 생성 마커들 지도에 출력
        markerList[0].width = markerSize
        markerList[0].height = markerSize
        markerList[0].captionText = restaurant_name


        // 해당 마커 위치로 지도 이동
        naverMap.moveCamera(CameraUpdate.scrollAndZoomTo(markerList[0].position, 20.0))

        // 마커 크기 변경 // 애니메이션 추가
        if (markerList[0].icon == markerImage) {

            markerList[0].icon = markerPinImage
            ObjectAnimator.ofInt(markerList[0], "width", markerPinSize).start()
            ObjectAnimator.ofInt(markerList[0], "height", markerPinSize).start()

        } else if (markerList[0].icon == markerImageEmpty) {

            markerList[0].icon = markerPinImageEmpty
            ObjectAnimator.ofInt(markerList[0], "width", markerPinSize).start()
            ObjectAnimator.ofInt(markerList[0], "height", markerPinSize).start()

        }


        retro.getRestaurantInfo(UserId, id, long, lati)
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

        // 해당 마커 클릭 이벤트
        markerClickEvent(markerList[0], id, long, lati)


        // 위의 markerGoEvent와 디자인패턴 이용??
    }

    // 마커 클릭 시
    private fun markerClickEvent(marker: Marker, id: Long, long: Double, lati: Double) {
        marker.setOnClickListener {
            if (lastMarker != null) {
                if (lastMarker!!.icon == markerPinImage)
                    lastMarker!!.icon = markerImage
                else if (lastMarker!!.icon == markerPinImageEmpty)
                    lastMarker!!.icon = markerImageEmpty
                lastMarker!!.width = markerSize
                lastMarker!!.height = markerSize
            }

            Log.d("MARKER_INFO", "lati: $lati long: $long")
            Log.d("MARKER_INFO", "Restaurant id : $id")

            // 마커 클릭 시 하단 네비게이션바 제거
//            (activity as HomeActivity).hideBottomNavi(true)


            retro.getRestaurantInfo(UserId, id, long, lati)
                .enqueue(object : Callback<GetRestaurantInfoResult> {
                    override fun onResponse(
                        call: Call<GetRestaurantInfoResult>,
                        response: Response<GetRestaurantInfoResult>,
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


            if (marker.icon == markerImage) {

                marker.icon = markerPinImage
                ObjectAnimator.ofInt(marker, "width", markerPinSize).start()
                ObjectAnimator.ofInt(marker, "height", markerPinSize).start()

            } else if (marker.icon == markerImageEmpty) {

                marker.icon = markerPinImageEmpty
                ObjectAnimator.ofInt(marker, "width", markerPinSize).start()
                ObjectAnimator.ofInt(marker, "height", markerPinSize).start()

            }


            lastMarker = marker

            true
        }
    }

    // 식당 정보화면 생성
    fun createMarkerInfo(result: RestaurantInfo?) {
        Log.d("MARKER_INFO", "img_url : ${result?.img_url}")

        // 뒤로가기 콜백
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.bottomMarkerInfo.visibility == View.VISIBLE) {
                    if (searchGoFlag == 1) {
                        binding.searchView.show()
                        searchGoFlag = 0
                    }

                    binding.bottomMarkerInfo.removeAllViews()

                    binding.bottomRestaurantList.visibility = View.VISIBLE
                    binding.bottomMarkerInfo.visibility = View.GONE
                } else {
                    if (System.currentTimeMillis() - waitTime >= 1500) {
                        waitTime = System.currentTimeMillis()
                        Toast.makeText(context, "버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    } else {
                        finishAffinity(requireActivity())
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            binding.bottomMarkerInfo.findViewTreeLifecycleOwner()!!,
            callback
        )

        binding.bottomMarkerInfo.removeAllViews()

        binding.bottomRestaurantList.visibility = View.GONE
        binding.bottomMarkerInfo.visibility = View.VISIBLE

        layoutInflater.inflate(R.layout.bottom_marker_info, binding.bottomMarkerInfo, true)

        BottomSheetBehavior.from(binding.bottomMarkerInfo).state =
            BottomSheetBehavior.STATE_EXPANDED


        // 이미지
        if (result?.img_url == null)
            binding.bottomMarkerInfo.findViewById<CardView>(R.id.markerInfoCardView).visibility = View.GONE
        else
            Glide.with(this)
                .load(result?.img_url)
                .into(binding.bottomMarkerInfo.findViewById<ImageView>(R.id.bottomImageMarkerInfo))

        // 파티 버튼 클릭 리스너
        binding.bottomMarkerInfo.findViewById<Button>(R.id.bottomButtonParty).setOnClickListener {
            val intent = Intent(requireContext(), RestaurantPartyActivity::class.java)
            val goToRestaurantPartyList = goToRestaurantPartyList(
                result?.restaurant_id, result?.restaurant_name, result?.restaurant_address,
                result?.img_url ?: "", result?.link ?: "",
                result?.category ?: "",
                result?.phone ?: ""
            )
            intent.putExtra("restaurantInfoObject", goToRestaurantPartyList)
            startActivity(intent)
        }

        // 식당이름 클릭 시 웹 이동
        binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextName).setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(result?.link)))
        }

        // 스트링
        binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextName).text =
            result?.restaurant_name
        binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextAddress).text =
            result?.restaurant_address
        binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextPhone).text =
            result?.phone
        binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextCategory).text =
            result?.category
        // 메뉴 3개로 축소
        Log.d("MARKER_INFO", "menu : ${result?.menu.toString()}")
        if ((result?.menu != null) && (result.menu.isNotEmpty())) {
            var text = ""
            for (idx in result.menu) {
                if (idx.price == null) text += "${idx.name} \n"
                else text += "${idx.name} : ${idx.price} \n"
                text.substring(0, text.length - 1)
                binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextMenu).text = text
            }
        } else {
            binding.bottomMarkerInfo.findViewById<TextView>(R.id.bottomTextMenu).text =
                "메뉴 미제공 \n\n\n"
        }
        binding.bottomMarkerInfo.findViewById<Button>(R.id.bottomButtonParty).text =
//            "${result?.num_of_party.toString()}   파티!"
            "파티!"

    }

    // 위치 권한 획득
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


    fun dpToPx(dp: Float, context: Context?): Float {
        val displayMetrics = context?.resources?.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics)
    }
}