package com.bapool.bapool.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bapool.bapool.adapter.RestaurantPartyAdapter
import com.bapool.bapool.databinding.ActivityRestaurantPartyBinding
import com.bapool.bapool.databinding.JoinpartyCustomDialogBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.*
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RestaurantPartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestaurantPartyBinding
    private lateinit var resGrpAdapter: RestaurantPartyAdapter
    lateinit var resGrpRv: RecyclerView
    lateinit var restaurantPartyInfoObject: goToRestaurantPartyList
    var restaurantPartiesInfo : List<ResPartyList> = arrayListOf()
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


        resGrpAdapter.itemClick = object : RestaurantPartyAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {
                val joinPartyDialog =
                    JoinpartyCustomDialogBinding.inflate(LayoutInflater.from(this@RestaurantPartyActivity))
                dialogBinding(restaurantPartiesInfo[position], joinPartyDialog)

                val mBuilder = AlertDialog.Builder(this@RestaurantPartyActivity)
                    .setView(joinPartyDialog.root)
                    .setNegativeButton("취소") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("참여") { dialog, _ ->
                        confirmParticipatePartyDialog()
                    }
                mBuilder.show()

            }
        }
    }

    fun confirmParticipatePartyDialog(){

        val mBuilder = AlertDialog.Builder(this@RestaurantPartyActivity)
            .setMessage("파티에 참여하시겠습니까?")
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("확인") { dialog, _ ->
                participatePartyUser()
                // 파티 채팅창으로 이동해야함.
                
            }
        mBuilder.show()
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
                            restaurantPartiesInfo = (partyResult?.parties ?: "") as List<ResPartyList>
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
        var item = participateParty(11)

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



    //다이얼로그
    fun dialogBinding(item: ResPartyList, binding: JoinpartyCustomDialogBinding) {
        //차단유저 보이게하기
        if (item.has_block_user) {
            binding.ban.visibility = View.VISIBLE
        }
        //hashtag 보이게하기
        val hashtagList: List<Int> = item.party_hashtag
        if (hashtagList.isNotEmpty()) {
            binding.hashtagVisible.visibility = View.VISIBLE
            var count = 0
            for (item in hashtagList) {
                count++
                if (item == 1)
                    when (count) {
                        1 -> binding.hash1.visibility = View.VISIBLE
                        2 -> binding.hash2.visibility = View.VISIBLE
                        3 -> binding.hash3.visibility = View.VISIBLE
                        4 -> binding.hash4.visibility = View.VISIBLE
                        5 -> binding.hash5.visibility = View.VISIBLE
                    }
            }
        }

        val allNum = partiNum(item.participants, item.max_people)
        val allDate = dateRange(item.start_date)

        binding.partyName.text = item.party_name
        binding.partyMenu.text = item.menu
        binding.dateTime.text = allDate
        binding.participantsNum.text = allNum
        binding.detailText.text = item.detail
        binding.rating.text = item.user_rating.toString()


    }

    fun dateRange(startDate: String): String {
        val start_date = LocalDateTime.parse(startDate)

        val formatterStart = DateTimeFormatter.ofPattern("MMM d일, H시 mm분")
        val range: String =
            "${start_date.format(formatterStart)}"
        return range

    }


    //참여인원, 정원 String으로 한줄 만들기
    fun partiNum(participants: Int, max_people: Int): String {
        var allNum: String = "${participants} / ${max_people}"
        return allNum
    }




}