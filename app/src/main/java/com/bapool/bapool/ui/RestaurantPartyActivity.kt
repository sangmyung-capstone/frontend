package com.bapool.bapool.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.RestaurantPartyAdapter
import com.bapool.bapool.databinding.ActivityRestaurantPartyBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantPartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantPartyBinding
    private lateinit var resGrpAdapter: RestaurantPartyAdapter
    lateinit var resGrpRv: RecyclerView
    lateinit var restaurantPartyInfoObject: goToRestaurantPartyList
    val restaurantId: Long = 1470337852

    val retro = ServerRetrofit.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantPartyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        restaurantPartyInfoObject =
            intent.getSerializableExtra("restaurantInfoObject") as goToRestaurantPartyList
        initializeVari()
        listener()
        retrofit()

    }

    //버튼초기화
    fun initializeVari() {
        resGrpRv = binding.resGrpRv
        resGrpAdapter = RestaurantPartyAdapter(this)


        binding.resName.setText(restaurantPartyInfoObject.name)
        binding.restaurantLocation.setText(restaurantPartyInfoObject.address)

        retrofit()

    }

    //버튼 listener
    fun listener() {
        binding.goToMakeGrp.setOnClickListener {

            val intent = Intent(this, MakePartyActivity::class.java)
            intent.putExtra("restaurantInfoObject", restaurantPartyInfoObject)
            startActivity(intent)
        }
        binding.dummyBtn.setOnClickListener {
            participatePartyUser()
        }

    }

    //recyclerview 연결
    fun adapter() {
        resGrpRv.adapter = resGrpAdapter
        resGrpRv.layoutManager = LinearLayoutManager(this)
        resGrpAdapter.notifyDataSetChanged()
    }

    //retrofit
    fun retrofit() {

        //restaur
        retro.getResPartyList(UserId!!.toLong(), restaurantPartyInfoObject.restaurant_id!!.toLong())
            .enqueue(object : Callback<GetResPartyListResponse> {
                override fun onResponse(
                    call: Call<GetResPartyListResponse>,
                    response: Response<GetResPartyListResponse>,
                ) {

                    if (response.isSuccessful) {
                        // 정상적으로 통신이 성공된 경우
                        response.body()?.let { result ->
                            val partyResult = result?.result

                            if(partyResult?.parties.isNullOrEmpty()){
                                binding.notifyNoParty.visibility = View.VISIBLE
                            }
                            resGrpAdapter.resName = partyResult?.restaurant_name ?: ""
                            resGrpAdapter.resGroup =
                                (partyResult?.parties ?: "") as List<ResPartyList>
                            Log.d("shRetrofitSE", partyResult.toString())
                            adapter()

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


    //파티참여 retrofit
    fun participatePartyUser() {
        var item = participateParty(5)

        retro.participateParty(5, item)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        Log.d("participateParty", response.body().toString())
                    } else {
                        Log.d("participateParty", response.errorBody().toString())

                    }
                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
                    Log.d("participateParty", "실패")
                }
            })

    }

}