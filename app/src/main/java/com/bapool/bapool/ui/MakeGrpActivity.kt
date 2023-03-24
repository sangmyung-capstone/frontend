package com.bapool.bapool.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Toast
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.ActivityMakeGrpBinding
import com.bapool.bapool.retrofit.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.max


class MakeGrpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeGrpBinding
    val hastagList = ArrayList(Collections.nCopies(5, 0))
    lateinit var maxPeople: NumberPicker
    val retro = RetrofitService.create()
    val userId: Long = 2
    val retaurantId = 1
    val imgUrl = "ImageUrl"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeGrpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var resName = intent.getStringExtra("resName")
        binding.resName.setText(resName)


        initializeVari()
        listener()

    }


    fun initializeVari() {
        //정원 numberpicker 최소 2 ~ 20까지 일단 설정
        maxPeople = binding.maxPeople
        maxPeople.maxValue = 20
        maxPeople.minValue = 2

    }

    fun listener() {
        //hashtag 선택 listener
        binding.hash1.setOnClickListener {
            val image1 = binding.hash1
            val currentState = image1.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image1.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(0, 0)
            } else {
                image1.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(0, 1)
            }
        }
        binding.hash2.setOnClickListener {
            val image2 = binding.hash2
            val currentState = image2.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image2.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(1, 0)
            } else {
                image2.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(1, 1)
            }
        }
        binding.hash3.setOnClickListener {
            val image3 = binding.hash3
            val currentState = image3.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image3.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(2, 0)
            } else {
                image3.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(2, 1)
            }
        }
        binding.hash4.setOnClickListener {
            val image4 = binding.hash4
            val currentState = image4.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image4.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(3, 0)
            } else {
                image4.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(3, 1)
            }
        }
        binding.hash5.setOnClickListener {
            val image5 = binding.hash5
            val currentState = image5.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image5.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(4, 0)
            } else {
                image5.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(4, 1)
            }
        }
        //모임시작 날짜 정하기
        binding.startDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog =
                DatePickerDialog(this, { view, selectedYear, selectedMonth, selectedDay ->

                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"


                    //시작시간 정할때 끝나는 날짜까지 지정해줌
                    binding.startDate.setText(selectedDate)
                    binding.endDate.setText(selectedDate)
                }, year, month, day)
            //오늘부터 최대 1년 뒤까지 날짜 설정 가능
            val datePicker = datePickerDialog.datePicker
            datePicker.minDate = System.currentTimeMillis()
            datePicker.maxDate =
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)
            datePickerDialog.show()
        }
        //모임 시작 시간 정하기
        binding.startTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { view, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d : %02d", selectedHour, selectedMinute)

                //시작시간 정할때 끝시간 +2시간으로 지정해줌
                binding.startTime.setText(selectedTime)
                binding.endTime.setText(
                    String.format("%02d : %02d", (selectedHour + 2) % 24, selectedMinute))
            }, hour, minute, true)
            timePickerDialog.show()
        }
        //모임 끝나는 날짜 정하기
        binding.endDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            //모임 끝나는 날짜가 시작날짜보다 뒤로 나와야함.
            //모임 시작날짜를 정한 이후 클릭이 가능하도록 만들어야함.
//            val dateFormat =
//                SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
//            val minDate = dateFormat.parse(binding.startDate.text.toString())

            val datePickerDialog =
                DatePickerDialog(this, { view, selectedYear, selectedMonth, selectedDay ->
                    // Do something with the selected date
                    val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    Log.d("hastagList", "Selected date: $selectedDate")
                    binding.endDate.setText(selectedDate.toString())
                }, year, month, day)
            //오늘부터 최대 1년 뒤까지 날짜 설정 가능
            val datePicker = datePickerDialog.datePicker

            datePicker.minDate = System.currentTimeMillis()
            datePicker.maxDate =
                System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)
            datePickerDialog.show()
        }
        //모임 끝나는 시간 정하기
        binding.endTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            //모임 끝나는 시간이 모임 시작시간보다 뒤로 있어야함
            val timePickerDialog = TimePickerDialog(this, { view, selectedHour, selectedMinute ->
                val selectedTime = String.format("%02d : %02d", selectedHour, selectedMinute)
                binding.endTime.setText(selectedTime)
            }, hour, minute, true)

            timePickerDialog.show()
        }


        //취소버튼 클릭
        binding.cancelButton.setOnClickListener {
            Log.d("hastagList", hastagList.toString())
            Log.d("hastagList", maxPeople.value.toString())
            Log.d("hastagList", binding.grpNameText.text.toString())
            Log.d("hastagList", binding.detail.text.toString())
            Log.d("hastagList", binding.menuText.text.toString())
            Log.d("dateString", binding.startDate.text.toString())
        }

        //그룹생성버튼, 그룹생성정보를 retrofit post로 넘겨줌
        binding.makeGrpButton.setOnClickListener {
            if (binding.grpNameText.text.isNullOrBlank()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("그룹명 비어있음")
                builder.setMessage("그룹명이 비어있다구")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                Log.d("AlertDialog", "그룹명")

            } else if (binding.menuText.text.isNullOrBlank()) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("상세메뉴 비어있음")
                builder.setMessage("상세메뉴 비어있다구")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                Log.d("AlertDialog", "상세메뉴")

            } else if (binding.startDate.text.toString() == "시작날짜") {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("시작날짜 비어있음")
                builder.setMessage("시작날짜 비어있다구")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                Log.d("AlertDialog", "시작날짜")

            } else if (binding.startTime.text.toString() == "시작시간") {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("시작시간 비어있음")
                builder.setMessage("시작시간 비어있다구")
                builder.setPositiveButton("확인") { dialog, which ->
                }
                val dialog = builder.create()
                dialog.show()
                Log.d("AlertDialog", "시작시간")
            } else {
                val makeGrpInstance =
                    PostMakeGrpRequest(retaurantId,
                        binding.grpNameText.text.toString(),
                        maxPeople.value,
                        binding.startDate.text.toString(),
                        binding.endDate.text.toString(),
                        binding.menuText.text.toString(),
                        imgUrl,
                        hastagList,
                        binding.detail.text.toString())

                retrofit(makeGrpInstance)
            }


        }
    }


    fun retrofit(makeGrp: PostMakeGrpRequest) {
        retro.makeGrp(userId, makeGrp).enqueue(object : Callback<PostMakeGrpResponse> {
            override fun onResponse(
                call: Call<PostMakeGrpResponse>,
                response: Response<PostMakeGrpResponse>,
            ) {
                if (response.isSuccessful) {
                    var result: PostMakeGrpResponse? =
                        response.body()
                    Log.d("MKRetrofit", "onRequest 성공: $makeGrp")
                    Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())
                    Toast.makeText(this@MakeGrpActivity, "그룹생성", Toast.LENGTH_SHORT).show()
                    //group_id 넘겨줘야함 Intent할때
                } else {
                    Log.d("MKRetrofite", "onResponse 실패 :" + response.body().toString())
                }
            }

            override fun onFailure(call: Call<PostMakeGrpResponse>, t: Throwable) {
                Log.d("MKRetrofit", "그룹생성 레트로핏 fail")
                Log.d("MKRetrofit", "onResponse 실패 :" + t.toString())
            }
        })

    }


}