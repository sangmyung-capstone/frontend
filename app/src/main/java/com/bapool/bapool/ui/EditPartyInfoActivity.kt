package com.bapool.bapool.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityEditPartyInfoBinding
import com.bapool.bapool.databinding.CustomDatepickerBinding
import com.bapool.bapool.databinding.CustomTimepickerBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.FirebasePartyInfo
import com.bapool.bapool.retrofit.data.PatchEditPartyInfoResponse
import com.bapool.bapool.retrofit.data.PatchEditPartyInfoRequest
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class EditPartyInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPartyInfoBinding
    val hastagList = ArrayList(Collections.nCopies(5, 0))
    lateinit var maxPeople: NumberPicker
    val retro = ServerRetrofit.create()
    val userId: Long = UserId!!
    val TAG = "EditPartyInfoActivity"

    var receivePartyInfo: FirebasePartyInfo = FirebasePartyInfo()
    var partyId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPartyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVari()
        listener()
    }


    fun initializeVari() {
        //정원 numberpicker 최소 2 ~ 20까지 일단 설정
        maxPeople = binding.maxPeople
        maxPeople.maxValue = 20
        maxPeople.minValue = 2


        receivePartyInfo = intent.getSerializableExtra("partyInfo") as FirebasePartyInfo
        partyId = intent.getStringExtra("partyId").toString()
        binding.grpNameText.setText(receivePartyInfo.groupName)
        binding.menuText.setText(receivePartyInfo.menu)
        binding.detail.setText(receivePartyInfo.groupDetail)
        binding.maxPeople.value = receivePartyInfo.maxNumberOfPeople
        binding.maxPeople.wrapSelectorWheel = false
        changeDateFormat(binding.startDateText, binding.startTimeText)
        setHashtagInfo()

    }

    fun listener() {
        hashtagClickListener(binding.hash1, 0)
        hashtagClickListener(binding.hash2, 1)
        hashtagClickListener(binding.hash3, 2)
        hashtagClickListener(binding.hash4, 3)
        hashtagClickListener(binding.hash5, 4)

        //모임시작 날짜 정하기
        binding.startDate.setOnClickListener {
            datePickerDialogCustom(System.currentTimeMillis(), 1)

        }
        //모임 시작 시간 정하기
        binding.startTime.setOnClickListener {
            timePickerDialogCustom(1)
        }

        //그룹생성버튼, 그룹생성정보를 retrofit post로 넘겨줌
        binding.makeGrpButton.setOnClickListener {

            Log.d("asdfasdfadsf", binding.grpNameText.text.length.toString())
            Log.d("asdfasdfadsf", binding.menuText.text.isNullOrBlank().toString())

            Log.d("asdfasdfadsf", binding.menuText.text.length.toString())

            if (binding.grpNameText.text.isNullOrBlank()) {
                alterDialog("파티명을 입력해주세요.")

            } else if (binding.grpNameText.length() > 10) {
                alterDialog("파티명은 10글자까지 가능합니다.")

            } else if (binding.menuText.text.isNullOrBlank()) {
                alterDialog("메뉴를 입력해주세요.")

            } else if (binding.menuText.length() > 10) {
                alterDialog("메뉴명은 10글자까지 가능합니다.")

            } else if (binding.maxPeople.value < receivePartyInfo.curNumberOfPeople) {
                alterDialog("현재 인원보다 적은 정원을 선택할 수 없습니다.")
            } else if (binding.startDateText.text.toString() == "시작날짜") {
                alterDialog("시작날짜를 입력해주세요.")

            } else if (binding.startTimeText.text.toString() == "시작시간") {
                alterDialog("시작시간을 입력해주세요.")
            } else {
                val endDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"
                val startDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"


                var hashtagList = arrayListOf<Int>()
                var count = 0
                for(data in hastagList){
                    count++
                    if(data != 0){
                        when (count) {
                            1 -> hashtagList.add(1)
                            2 -> hashtagList.add(2)
                            3 -> hashtagList.add(3)
                            4 -> hashtagList.add(4)
                            5 -> hashtagList.add(5)
                        }
                    }

                }


                val editPartyInstance =
                    PatchEditPartyInfoRequest(
                        partyId.toLong(),
                        binding.grpNameText.text.toString(),
                        maxPeople.value,
                        startDateLocal,
                        endDateLocal,
                        binding.menuText.text.toString(),
                        binding.detail.text.toString(),
                        hashtagList
                    )
                Log.d(TAG, editPartyInstance.toString())

                retrofit(editPartyInstance)
            }


        }
    }


    fun hashtagClickListener(image: ImageView, i: Int) {
        image.setOnClickListener {
            val currentState = image.background.constantState
            val normalState = getDrawable(R.drawable.custom_img_bg)?.constantState
            if (currentState == normalState) {
                image.setBackgroundResource(R.drawable.custom_img_bg_pressed)
                hastagList.set(i, 0)
            } else {
                image.setBackgroundResource(R.drawable.custom_img_bg)
                hastagList.set(i, 1)
            }
        }
    }


    fun retrofit(editParty: PatchEditPartyInfoRequest) {
        Log.d("MKRetrofit", editParty.toString())
        Log.d("MKRetrofit", userId.toString())
        retro.editParty(UserId!!.toLong(), editParty)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {
                    var result: PatchEditPartyInfoResponse? = response.body()

                    if (response.isSuccessful) {
                        Log.d("MKRetrofit", "onRequest 성공: $editParty")
                        Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())
                        finish()
                    } else {
                        Log.d("MKRetrofit", "onResponse 실패: " + response.body()?.code.toString())
                        Log.d("MKRetrofit", "onResponse 실패: " + response.body()?.message.toString())

                        Toast.makeText(
                            this@EditPartyInfoActivity,
                            "그룹 생성 오류 fail",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {

                    Toast.makeText(this@EditPartyInfoActivity, "그룹 생성 오류", Toast.LENGTH_SHORT)
                        .show()
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
                } else if (startOrEnd == 0) {
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
            } else if (startOrEnd == 0) {
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

    fun changeDateFormat(startDateText: TextView, startTimeText: TextView) {

        val inputDateTime = receivePartyInfo.startDate


        Log.d("sadfasfadsfasdfasdf", receivePartyInfo.toString())
        Log.d("sadfasfadsfasdfasdf", receivePartyInfo.startDate)

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
        val dateTime = LocalDateTime.parse(inputDateTime, formatter)

        val month = String.format("%02d", dateTime.monthValue)
        val day = String.format("%02d", dateTime.dayOfMonth)
        val year = dateTime.year.toString()

        val hour = String.format("%02d", dateTime.hour)
        val minute = String.format("%02d", dateTime.minute)

        val date = "${year}-${month}-${day}"
        val time = "$hour:$minute"

        startDateText.setText(date)
        startTimeText.setText(time)

    }

    fun setHashtagInfo() {
        val receiveHashtag = receivePartyInfo.hashTag
        val image1 = binding.hash1
        val image2 = binding.hash2
        val image3 = binding.hash3
        val image4 = binding.hash4
        val image5 = binding.hash5

        Log.d("asdfasdfasdf",receiveHashtag.toString())

        for (data in receiveHashtag) {
            when (data) {
                1 -> {
                    image1.setBackgroundResource(R.drawable.custom_img_bg)
                    hastagList.set(0, 1)

                }

                2 -> {
                    image2.setBackgroundResource(R.drawable.custom_img_bg)
                    hastagList.set(1, 1)

                }
                3 -> {
                    image3.setBackgroundResource(R.drawable.custom_img_bg)
                    hastagList.set(2, 1)

                }
                4 -> {
                    image4.setBackgroundResource(R.drawable.custom_img_bg)
                    hastagList.set(3, 1)

                }
                5 -> {
                    image5.setBackgroundResource(R.drawable.custom_img_bg)
                    hastagList.set(4, 1)

                }
                else -> Log.d("EditPartyInfoActivity", "error")

            }

        }
    }
}