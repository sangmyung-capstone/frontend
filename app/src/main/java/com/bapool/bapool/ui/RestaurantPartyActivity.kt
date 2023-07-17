package com.bapool.bapool.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.RestaurantPartyAdapter
import com.bapool.bapool.databinding.ActivityRestaurantPartyBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.GetResPartyListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantPartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantPartyBinding
    private lateinit var resGrpAdapter: RestaurantPartyAdapter
    lateinit var resGrpRv: RecyclerView
    var restaurantNameIntent: String = ""
    var restaurantLocationIntent: String = ""
    val userId: Long = 1
    val restaurantId: Long = 1470337852

    val retro = ServerRetrofit.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initializeVari()
        retrofit()
        listener()


    }

    //버튼초기화
    fun initializeVari() {
        resGrpRv = binding.resGrpRv
        resGrpAdapter = RestaurantPartyAdapter(this)

//        restaurantNameIntent = intent.getStringExtra("restaurantNameIntent")
//        restaurantLocationIntent = intent.getStringExtra("restaurantLocationIntent")
        binding.restaurantLocation.setText(restaurantLocationIntent)
        binding.resName.setText(restaurantNameIntent)


    }

    //버튼 listener
    fun listener() {
        binding.goToMakeGrp.setOnClickListener {

            val intent = Intent(this, MakePartyActivity::class.java)
            intent.putExtra("resName", restaurantNameIntent)
            startActivity(intent)
        }

    }

    //recyclerview 연결
    fun adapter() {
        resGrpRv.adapter = resGrpAdapter
        resGrpRv.layoutManager = LinearLayoutManager(this)
    }

    //retrofit
    fun retrofit() {

        retro.getResPartyList(userId, restaurantId)
            .enqueue(object : Callback<GetResPartyListResponse> {
                override fun onResponse(
                    call: Call<GetResPartyListResponse>,
                    response: Response<GetResPartyListResponse>,
                ) {
                    Log.d("shRetrofitSE", response.body().toString())

                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        response.body()?.let { result ->
//                            val partyResult = result?.result
//                            Log.d("sdkfjsadlfjasldf",partyResult.toString())
//                            resNameIntent = partyResult?.restaurant_name ?: ""
//                            resGrpAdapter.resName = partyResult?.restaurant_name ?: ""
//                            binding.resName.setText(partyResult?.restaurant_name ?: "")
//                            resGrpAdapter.resGroup = (partyResult?.parties ?: "") as List<ResPartyList>
//                            binding.restaurantName.setText(partyResult?.restaurant_name ?: "")
////                            Log.d("shRetrofitSE", partyResult.toString())
//                            adapter()
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("shRetrofitE", "onResponse 실패100")
                    }
                }

                override fun onFailure(call: Call<GetResPartyListResponse>, t: Throwable) {
                    Log.d("shRetrofitE", "onFailure 에러: " + t.message.toString());
                }
            })

    }


}