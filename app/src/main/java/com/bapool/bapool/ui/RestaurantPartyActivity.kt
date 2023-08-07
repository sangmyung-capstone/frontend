package com.bapool.bapool.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
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

    companion object {
        var RestaurantPartyActivityCompanion: RestaurantPartyActivity? = null
    }


    lateinit var resGrpRv: RecyclerView
    lateinit var restaurantPartyInfoObject: goToRestaurantPartyList
    var restaurantPartiesInfo: List<ResPartyList> = arrayListOf()

    val retro = ServerRetrofit.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantPartyBinding.inflate(layoutInflater)
        RestaurantPartyActivityCompanion = this

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



        resGrpAdapter.itemClick = object : RestaurantPartyAdapter.ItemClick {
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

                        if (restaurantPartiesInfo[position].participants == restaurantPartiesInfo[position].max_people) {
                            overCapacityDialog()
                        } else {
                            confirmParticipatePartyDialog(restaurantPartiesInfo[position].party_id.toString())
                        }

                    }
                mBuilder.show()
            }
        }
    }

    //정원 초과 알려주는 dialog
    fun overCapacityDialog(){
        val mBuilder = AlertDialog.Builder(this@RestaurantPartyActivity)
            .setMessage("파티의 인원이 꽉찼습니다!")
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }

        mBuilder.show()
    }

    //파티참여 물어보는 dialog
    fun confirmParticipatePartyDialog(party_id: String) {

        val mBuilder = AlertDialog.Builder(this@RestaurantPartyActivity)
            .setMessage("파티에 참여하시겠습니까?")
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("확인") { dialog, _ ->
                participatePartyUser(party_id)
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

    }

    //recyclerview 연결
    fun adapter() {
        resGrpRv.adapter = resGrpAdapter
        resGrpRv.layoutManager = LinearLayoutManager(this)
        resGrpAdapter.notifyDataSetChanged()

    }

    //retrofit
    fun retrofit() {


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

                            if (partyResult?.parties.isNullOrEmpty()) {
                                binding.notifyNoParty.visibility = View.VISIBLE
                            }
                            restaurantPartiesInfo =
                                (partyResult?.parties ?: "") as List<ResPartyList>
                            resGrpAdapter.resName = partyResult?.restaurant_name ?: ""
                            resGrpAdapter.resGroup =
                                (partyResult?.parties ?: "") as List<ResPartyList>
                            adapter()

                        }
                    } else {
                    }
                }

                override fun onFailure(call: Call<GetResPartyListResponse>, t: Throwable) {

                    Log.d("hghkhk",t.toString())

                }
            })
    }


    //파티참여 retrofit
    fun participatePartyUser(party_id: String) {
        var item = participateParty(party_id.toLong())

        retro.participateParty(UserId!!.toLong(), item)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        val intent = Intent(this@RestaurantPartyActivity,
                            ChattingAndPartyInfoMFActivity::class.java)
                        intent.putExtra("partyId", party_id)

                        startActivity(intent)

                    } else {

                    }
                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
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

        val formatterStart = DateTimeFormatter.ofPattern("MMM d일 H시 mm분")
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


