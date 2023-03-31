package com.bapool.bapool.ui.fragment

import android.Manifest
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.FragmentMapBinding
import com.bapool.bapool.retrofit.data.GetRestaurantsResult
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PolylineOverlay
import com.naver.maps.map.util.FusedLocationSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MapFragment : Fragment(), OnMapReadyCallback {
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var cameraPosition: CameraPosition
    private lateinit var bounds: LatLngBounds
    private lateinit var rect: String

    val retro = RetrofitService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
            binding.extendedFAB.hide()
        }

        /******************************************************************************************/
        /*
        /*** 상단 검색 바 ***/
        //View - 변수 연결
        val lv = binding.mListView
        val searchBar = binding.searchBar
        searchBar.setHint("Search")
        //음성검색모드 끄기
        searchBar.setSpeechMode(false)
        //검색어 목록 넣기
        var galaxies = arrayOf(
            "Sombrero",
            "Cartwheel",
            "Pinwheel",
            "StarBust",
            "Whirlpool",
            "Ring Nebular",
            "Own Nebular",
            "Centaurus A",
            "Virgo Stellar Stream",
            "Canis Majos Overdensity",
            "Mayall's Object",
            "Leo",
            "Milky Way",
            "IC 1011",
            "Messier 81",
            "Andromeda",
            "Messier 87"
        )

        val adapter =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, galaxies)
        //리스트뷰 초기에 안보이게 설정
        lv.visibility = View.INVISIBLE
        //SearchBar와 ListView 연동
        lv.adapter = adapter
        searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onButtonClicked(buttonCode: Int) {
                TODO("Not yet implemented")
            }

            //검색창 누른 상태 여부 확인
            override fun onSearchStateChanged(enabled: Boolean) {
                //맞으면 리스트뷰 보이게 설정
                if (enabled) {
                    lv.visibility = View.VISIBLE
                } else { //아니면 안 보이게
                    lv.visibility = View.INVISIBLE
                }
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                TODO("Not yet implemented")
            }

        })

        searchBar.addTextChangeListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            //검색어 변경하면 ListView 내용 변경
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

        })

        //ListView 내의 아이템 누르면 Toast 발생
        lv.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(
                    context,
                    adapter.getItem(position)!!.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
         */
        /******************************************************************************************/

        // 네비게이션 바 간 스위칭 관련 리스너
//        listener()

        return binding.root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ui setting
        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true
        // 최대 줌 레벨 설정
        naverMap.maxZoom = 19.0

        this.naverMap = naverMap
        // 실행된 네이버 지도에 locationSource 연결
        naverMap.locationSource = locationSource
        // 현 위치로 맵 시작
        naverMap.locationTrackingMode = LocationTrackingMode.Follow


        naverMap.addOnCameraChangeListener { reason, _ ->
            // 지도가 이동 상태 시 콜백함수
            if (reason == -1) binding.extendedFAB.show()   // 사용자 움직임 시 reason == -1
        }

        naverMap.addOnCameraIdleListener {
            // 지도가 이동 후 대기 상태 시 콜백함수
        }

        markerInit()
    }

    private fun mapInit() {
        // Framelayout에 네이버 지도 띄우기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 현위치 반환 구현체 locationSource 선언
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }

    private fun markerInit() {
        cameraPosition = naverMap.cameraPosition

        rect =
            "${naverMap.contentBounds.southWest.longitude},${naverMap.contentBounds.southWest.latitude},${naverMap.contentBounds.northEast.longitude},${naverMap.contentBounds.northEast.latitude}"

        /*
        val polyline = PolylineOverlay()
        polyline.coords = listOf(
            naverMap.contentBounds.southWest,
            naverMap.contentBounds.northEast
        )
        polyline.map = naverMap
         */

        Log.d("MYTAG", "rect : $rect")
        Log.d("MYTAG", "now bounds : ${naverMap.contentBounds}")
        Log.d("MYTAG", "now camera : $cameraPosition")
        //------------------------------------
        retro.getRestaurants(rect).enqueue(object : Callback<GetRestaurantsResult> {
            override fun onResponse(
                call: Call<GetRestaurantsResult>,
                response: Response<GetRestaurantsResult>
            ) {
                //Log.d("MYTAG", response.body().toString())
                val markerList: ArrayList<Marker> = arrayListOf<Marker>()
                for (i in 0 until response.body()!!.body.size) {
                    markerList.add(i, Marker())
                    markerList[i].isHideCollidedSymbols = true
                    markerList[i].position =
                        LatLng(
                            response.body()!!.body[i].y.toDouble(),
                            response.body()!!.body[i].x.toDouble()
                        )
                    markerList[i].map = naverMap
                    markerList[i].width = Marker.SIZE_AUTO
                    markerList[i].height = Marker.SIZE_AUTO
                    markerList[i].captionText = response.body()!!.body[i].place_name
                    /*
                    //group이 있는 마커 표현
                    if (response.body()!!.body[i].num_of_group != 0)
                    markerList[i].icon = MarkerIcons.YELLOW
                     */
                    markerList[i].setOnClickListener {
//                        val bottomSheetDialog = BottomSheetDialog.
                        /*
                        // 마커를 1.5배 크게 만드는 애니메이션
                        val animator = ValueAnimator.ofFloat(1f, 3.0f)
                        animator.duration = 1500
                        animator.addUpdateListener { valueAnimator ->
                            val value = valueAnimator.animatedValue as Float
                            markerList[i].width = (markerList[i].width * value).toInt()
                            markerList[i].height = (markerList[i].height * value).toInt()
                        }
                        animator.start()
                         */

                        // 해당 마커 위치로 지도 이동
                        val cameraUpdate: CameraUpdate =
//                            CameraUpdate.fitBounds(markerList[i].map!!.contentBounds)     // 추가 작업 필요
                            CameraUpdate.scrollAndZoomTo(markerList[i].position, 15.0)
                        naverMap.moveCamera(cameraUpdate)
                        true
                    }
                }
            }

            override fun onFailure(call: Call<GetRestaurantsResult>, t: Throwable) {
                Log.d("MYTAG", t.message.toString())
                Log.d("MYTAG", "FAIL")
            }
        })
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
                Toast.makeText(context, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT)
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

    // 네비게이션 바 간 스위칭 관련 리스너
//    fun listener() {
//
//    }

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