package com.bapool.bapool.ui.fragment

import android.Manifest
import android.graphics.Color
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
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
    private var radius: Int = 0

    val retro = RetrofitService.create()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        binding.floatingActionButton.hide()

        // 현 위치에서 검색 터치 시 // FAB
        binding.floatingActionButton.setOnClickListener {
            markerInit()
            binding.floatingActionButton.hide()
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

        // Framelayout에 네이버 지도 띄우기
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        // 현위치 반환 구현체 locationSource 선언
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // 위치 권한 요청
        requestPermissions()

        // 네비게이션 바 간 스위칭 관련 리스너
        listener()

        return binding.root
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        // ui setting
        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlEnabled = false
        uiSettings.isLocationButtonEnabled = true

        this.naverMap = naverMap
        // 실행된 네이버 지도에 locationSource 연결
        naverMap.locationSource = locationSource


        naverMap.addOnCameraChangeListener { reason, animated ->
            // 지도가 이동될 때마다 호출되는 코드
            binding.floatingActionButton.show()
        }

        markerInit()

        /*
        // 마커 추가 테스트
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740)
        marker.map = naverMap
        marker.width = Marker.SIZE_AUTO
        marker.height = Marker.SIZE_AUTO
        marker.captionText = "테스트용 1"

        val marker2 = Marker()
        marker2.position = LatLng(37.4447533294745, 126.702653350562)
        marker2.map = naverMap
        marker2.icon = MarkerIcons.YELLOW
//        marker2.iconTintColor = Color.YELLOW
        marker2.width = Marker.SIZE_AUTO
        marker2.height = Marker.SIZE_AUTO
        marker2.captionText = "테스트용 2"
         */



    }

    private fun markerInit() {
        cameraPosition = naverMap.cameraPosition

        bounds = naverMap.contentBounds
        radius = ((bounds.northWest).distanceTo(bounds.southEast).toInt()) / 2
        Log.i("MYTAG", "radius : $radius m")
        Log.d("MYTAG", "now bounds : ${naverMap.contentBounds}")
        Log.d("MYTAG", "now camera : $cameraPosition")
        //------------------------------------
        retro.getRestaurants(
            cameraPosition.target.longitude,
            cameraPosition.target.latitude,
            100
        ).enqueue(object : Callback<GetRestaurantsResult> {
            override fun onResponse(
                call: Call<GetRestaurantsResult>,
                response: Response<GetRestaurantsResult>
            ) {
                //Log.d("MYTAG", response.body().toString())
                /*
//                    Log.d(
//                        "MYTAG",
//                        "받은 body 리스트의 개수 : " + response.body()!!.body.size.toString()
//                    )
//                    Log.d(
//                        "MYTAG",
//                        "해당 식당의 존재 그룹 개수 :" + response.body()!!.body[0].num_of_group.toString()
//                    )
                // 받은 result 이용 지도에 마커 띄우기
//                    val num_of_marker = response.body()!!.body.size
                 */
                val markerList: ArrayList<Marker> = arrayListOf<Marker>()
                for (i in 0 until response.body()!!.body.size) {
                    markerList.add(i, Marker())
                    markerList[i].position =
                        LatLng(response.body()!!.body[i].res_y, response.body()!!.body[i].res_x)
                    markerList[i].map = naverMap
                    markerList[i].width = Marker.SIZE_AUTO
                    markerList[i].height = Marker.SIZE_AUTO
                    markerList[i].captionText = "테스트용 ${i + 1}"
                    if (response.body()!!.body[i].num_of_group != 0)
                        markerList[i].icon = MarkerIcons.YELLOW
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
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(context, "Permission Denied\n$deniedPermissions", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // TedPermission 라이브러리 // 위치 권한 요청
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setRationaleTitle("위치권한 요청!")
            .setRationaleMessage("현재 위치로 이동하기 위해 위치권한이 필요해요!!")
            .setPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .check()
    }

    // 네비게이션 바 간 스위칭 관련 리스너
    fun listener() {
        binding.fragmentGroup.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapFragment_to_groupFragment)
        }
        binding.fragmentMypage.setOnClickListener {
            it.findNavController().navigate(R.id.action_mapFragment_to_mypageFragment)
        }
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