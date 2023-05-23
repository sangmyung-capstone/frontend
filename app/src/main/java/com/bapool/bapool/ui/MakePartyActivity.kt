package com.bapool.bapool.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.Toast
import com.bapool.bapool.R
import com.bapool.bapool.RetrofitService
import com.bapool.bapool.databinding.ActivityMakePartyBinding
import com.bapool.bapool.databinding.CustomDatepickerBinding
import com.bapool.bapool.databinding.CustomTimepickerBinding
import com.bapool.bapool.retrofit.data.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MakePartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakePartyBinding
    val hastagList = ArrayList(Collections.nCopies(5, 0))
    lateinit var maxPeople: NumberPicker
    val retro = RetrofitService.create()
    val userId: Long = 2
    val retaurantId = 1
    val imgUrl = "ImageUrl"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePartyBinding.inflate(layoutInflater)
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
            datePickerDialogCustom(System.currentTimeMillis(), 1)

        }
        //모임 시작 시간 정하기
        binding.startTime.setOnClickListener {
            timePickerDialogCustom(1)
        }


        //모임 끝나는 날짜 정하기
        binding.endDate.setOnClickListener {
            if (binding.startDateText.text.toString() == "시작날짜") {
                alterDialog("시작날짜를 정해주세요.")
            } else {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
                val minDate = dateFormat.parse("${binding.startDateText.text}")
                val customDatePickerBinding =
                    CustomDatepickerBinding.inflate(LayoutInflater.from(baseContext))
                datePickerDialogCustom(minDate.time, 0)

            }

        }
        //모임 끝나는 시간 정하기
        binding.endTime.setOnClickListener {
            timePickerDialogCustom(0)
        }


        //취소버튼 클릭
        binding.cancelButton.setOnClickListener {
            Log.d("hastagList", hastagList.toString())
            Log.d("hastagList", maxPeople.value.toString())
            Log.d("hastagList", binding.grpNameText.text.toString())
            Log.d("hastagList", binding.detail.text.toString())
            Log.d("hastagList", binding.menuText.text.toString())
        }

        //그룹생성버튼, 그룹생성정보를 retrofit post로 넘겨줌
        binding.makeGrpButton.setOnClickListener {
            if (binding.grpNameText.text.isNullOrBlank()) {
                alterDialog("그룹명을 입력해주세요.")

            } else if (binding.menuText.text.isNullOrBlank()) {
                alterDialog("상세메뉴를 입력해주세요.")

            } else if (binding.startDateText.text.toString() == "시작날짜") {
                alterDialog("시작날짜를 입력해주세요.")

            } else if (binding.startTimeText.text.toString() == "시작시간") {
                alterDialog("시작시간을 입력해주세요.")
            } else if (compareTime(binding.startDateText.text.toString(),
                    binding.endDateText.text.toString(),
                    binding.startTimeText.text.toString(),
                    binding.endTimeText.text.toString())
            ) {
                alterDialog("끝나는 시간이 시작 시간보다 작습니다.")
            } else {
                val makeGrpInstance =
                    PostMakePartyRequest(retaurantId,
                        binding.grpNameText.text.toString(),
                        maxPeople.value,
                        binding.startDateText.text.toString(),
                        binding.endDateText.text.toString(),
                        binding.menuText.text.toString(),
                        imgUrl,
                        hastagList,
                        binding.detail.text.toString())
                val intent = Intent(this,ChattingAndPartyInfoActivity::class.java)
                startActivity(intent)
                retrofit(makeGrpInstance)
            }


        }
    }


    fun retrofit(makeGrp: PostMakePartyRequest) {
        retro.makeGrp(userId, makeGrp).enqueue(object : Callback<PostMakePartyResponse> {
            override fun onResponse(
                call: Call<PostMakePartyResponse>,
                response: Response<PostMakePartyResponse>,
            ) {
                if (response.isSuccessful) {
                    var result: PostMakePartyResponse? =
                        response.body()
                    Log.d("MKRetrofit", "onRequest 성공: $makeGrp")
                    Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())
                    Toast.makeText(this@MakePartyActivity, "그룹생성", Toast.LENGTH_SHORT).show()
                    //group_id 넘겨줘야함 Intent할때
                } else {
                    Log.d("MKRetrofite", "onResponse 실패 :" + response.body().toString())
                }
            }

            override fun onFailure(call: Call<PostMakePartyResponse>, t: Throwable) {
                Log.d("MKRetrofit", "그룹생성 레트로핏 fail")
                Log.d("MKRetrofit", "onResponse 실패 :" + t.toString())
            }
        })

    }

    //DatePickerDialog
    fun datePickerDialogCustom(
        minDate: Long,
        startOrEnd: Int,
    ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val customDatePickerBinding =
            CustomDatepickerBinding.inflate(LayoutInflater.from(baseContext))

        val datePickerDialog =
            DatePickerDialog(this, { _, _, _, _ ->
                val selectedYear = customDatePickerBinding.datePicker.year
                val selectedMonth = customDatePickerBinding.datePicker.month
                val selectedDay = customDatePickerBinding.datePicker.dayOfMonth
                val selectedDate =
                    String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                if (startOrEnd == 1) {
                    binding.startDateText.setText(selectedDate)
                    binding.endDateText.setText(selectedDate)
                } else if (startOrEnd == 0) {
                    binding.endDateText.setText(selectedDate)
                }

            }, year, month, day)
        //오늘부터 최대 1년 뒤까지 날짜 설정 가능
        datePickerDialog.setView(customDatePickerBinding.root)
        customDatePickerBinding.datePicker.minDate = minDate
        customDatePickerBinding.datePicker.maxDate =
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(365)

        datePickerDialog.show()
    }

    //TimePickerDialog
    fun timePickerDialogCustom(startOrEnd: Int) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val customTimePickerBinding =
            CustomTimepickerBinding.inflate(LayoutInflater.from(baseContext))

        val timePickerDialog = TimePickerDialog(this, { _, _, _ ->
            val selectedHour = customTimePickerBinding.timePicker.hour
            val selectedMinute = customTimePickerBinding.timePicker.minute
            val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
            //시작시간 정할때 끝시간 +2시간으로 지정해줌
            if (startOrEnd == 1) {
                binding.startTimeText.setText(selectedTime)
                binding.endTimeText.setText(
                    String.format("%02d:%02d", (selectedHour + 2) % 24, selectedMinute))
            } else if (startOrEnd == 0) {
                binding.endTimeText.setText(selectedTime)
            }

        }, hour, minute, true)
        timePickerDialog.setView(customTimePickerBinding.root)
        customTimePickerBinding.timePicker.setIs24HourView(true)
        timePickerDialog.show()

    }

    //dialog 양식
    fun alterDialog(exceptionalString: String) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(exceptionalString)
        builder.setPositiveButton("확인") { dialog, which ->
        }
        val dialog = builder.create()
        dialog.show()
    }


    private fun compareTime(
        startDate: String,
        endDate: String,
        startTimeUnit: String,
        endTimeUnit: String,
    ): Boolean {
        if (startDate == endDate) {
            val resultNum = chageTimeForm(
                startTimeUnit,
                endTimeUnit
            )
            when {
                resultNum < 0 -> {
                    return false
                }
                resultNum > 0 -> {
                    return true
                }
                else -> {
                    return false
                }
            }
        } else {
            return false
        }
        return false
    }

    fun chageTimeForm(startTimeUnit: String, endTimeUnit: String): Int {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar1.time = timeFormat.parse(startTimeUnit)
        calendar2.time = timeFormat.parse(endTimeUnit)

        Log.d("timesetting", calendar1.toString())
        Log.d("timesetting", calendar1.toString())
        Log.d("timesetting", calendar1.before(calendar2).toString())

        val cmp = calendar1.compareTo(calendar2)
        return cmp

    }
}
