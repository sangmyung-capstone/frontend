package com.bapool.bapool.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.adapter.RestaurantPartyAdapter
import com.bapool.bapool.databinding.ActivityRestaurantPartyBinding
import com.bapool.bapool.retrofit.data.GetResGroupListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantPartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantPartyBinding
    private lateinit var resGrpAdapter: RestaurantPartyAdapter
    lateinit var resGrpRv: RecyclerView
    var resNameIntent: String = ""
    val userId: Long = 2
    val restaurantId: Long = 10

    val retro = RetrofitService.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        retrofit()
        initializeVari()
        listener()


    }

    //버튼초기화
    fun initializeVari() {
        resGrpRv = binding.resGrpRv
        resGrpAdapter = RestaurantPartyAdapter(this)

    }

    //버튼 listener
    fun listener() {
        binding.goToMakeGrp.setOnClickListener {
            val intent = Intent(this, MakePartyActivity::class.java)
            intent.putExtra("resName", resNameIntent)
            startActivity(intent)
        }
//        resGrpAdapter.itemClick = object : ResGrpListAdapter.ItemClick {
//            override fun onClick(view: View, position: Int) {
//                Toast.makeText(baseContext,)
//            }
//
//        }
    }

    //recyclerview 연결
    fun adapter() {
        resGrpRv.adapter = resGrpAdapter
        resGrpRv.layoutManager = LinearLayoutManager(this)
    }

    //retrofit
    fun retrofit() {

        retro.getResGrpList(userId, restaurantId)
            .enqueue(object : Callback<GetResGroupListResponse> {
                override fun onResponse(
                    call: Call<GetResGroupListResponse>,
                    response: Response<GetResGroupListResponse>,
                ) {
                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        response.body()?.let { result ->
                            resNameIntent = result.restaurant_name
                            resGrpAdapter.resName = result.restaurant_name
                            binding.resName.setText(result.restaurant_name)
                            resGrpAdapter.resGroup = result.groups
                            adapter()
                        }

                        Log.d("shRetrofitN", "onResponse 성공: " + resGrpAdapter.resName.toString());
                        Log.d("shRetrofitG", "onResponse 성공: " + resGrpAdapter.resGroup.toString());
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("shRetrofitSE", "onResponse 실패")
                    }
                }

                override fun onFailure(call: Call<GetResGroupListResponse>, t: Throwable) {
                    Log.d("shRetrofitE", "onFailure 에러: " + t.message.toString());
                }
            })

    }


}