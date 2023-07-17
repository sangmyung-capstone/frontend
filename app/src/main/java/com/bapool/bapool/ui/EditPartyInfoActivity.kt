package com.bapool.bapool.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class EditPartyInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPartyInfoBinding
    val hastagList = ArrayList(Collections.nCopies(5, 0))
    lateinit var maxPeople: NumberPicker
    val retro = ServerRetrofit.create()
    val userId: Long = 3
    val partyId: Long = 8
    val TAG = "EditPartyInfoActivity"

    var receivePartyInfo: FirebasePartyInfo = FirebasePartyInfo()
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
        binding.grpNameText.setText(receivePartyInfo.groupName)
        binding.menuText.setText(receivePartyInfo.groupMenu)
        binding.detail.setText(receivePartyInfo.groupDetail)
        changeDateFormat(binding.startDateText, binding.startTimeText)
        setHashtagInfo()


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
            } else {
                val endDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"
                val startDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"

                val editPartyInstance =
                    PatchEditPartyInfoRequest(
                        partyId,
                        binding.grpNameText.text.toString(),
                        maxPeople.value,
                        startDateLocal,
                        endDateLocal,
                        binding.menuText.text.toString(),
                        binding.detail.text.toString(),
                    )
                Log.d(TAG, editPartyInstance.toString())

                retrofit(editPartyInstance)
            }


        }
    }


    fun retrofit(editParty: PatchEditPartyInfoRequest) {

        retro.editParty(userId, editParty).enqueue(object : Callback<PatchEditPartyInfoResponse> {
            override fun onResponse(
                call: Call<PatchEditPartyInfoResponse>,
                response: Response<PatchEditPartyInfoResponse>,
            ) {
                var result: PatchEditPartyInfoResponse? = response.body()

                if (response.isSuccessful) {
                    Log.d("MKRetrofit", "onRequest 성공: $editParty")
                    Log.d("MKRetrofit", "onResponse 성공: " + result?.toString())

//                    val intent =
//                        Intent(this@EditPartyInfoActivity,
//                            ChattingAndPartyInfoMFActivity::class.java)
////                intent.putExtra("currentUserId", "userId2")//현재 유저의 userId로 value값 교체
////                intent.putExtra("partyId", "groupId2") // result 안의 party_id 값으로 value값 교체
//                    startActivity(intent)


                } else {
                    Log.d("MKRetrofit", "onResponse 실패: " + response.errorBody().toString())

                    Toast.makeText(this@EditPartyInfoActivity, "그룹 생성 오류 fail", Toast.LENGTH_SHORT)
                        .show()

                }


            }

            override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {
//                    val responseCode = response.code()
//                    val errorBody = response.errorBody()?.string()
//                    Log.d("MKRetrofit", response.toString())
//
//                    // 실패한 응답 처리
//                    Log.d("MKRetrofit", "응답 실패. 응답 코드: $responseCode, 에러 메시지: $errorBody")

                Toast.makeText(this@EditPartyInfoActivity, "그룹 생성 오류", Toast.LENGTH_SHORT).show()
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

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
        val dateTime = LocalDateTime.parse(inputDateTime, formatter)

        val month = String.format("%02d", dateTime.monthValue)
        val day = String.format("%02d", dateTime.dayOfMonth)
        val year = dateTime.year.toString()

        val hour = String.format("%02d", dateTime.hour)
        val minute = String.format("%02d", dateTime.minute)

// Output the extracted components
        val date = "${year}-${month}-${day}"
        val time = "$hour:$minute"

        startDateText.setText(date)
        startTimeText.setText(time)

    }

    fun setHashtagInfo(){
        val receiveHashtag = receivePartyInfo.hashTag
        val image1 = binding.hash1
        val image2 = binding.hash2
        val image3 = binding.hash3
        val image4 = binding.hash4
        val image5 = binding.hash5

        for (data in receiveHashtag) {
            when (data) {
                1 -> image1.setBackgroundResource(R.drawable.custom_img_bg)
                2 -> image2.setBackgroundResource(R.drawable.custom_img_bg)
                3 -> image3.setBackgroundResource(R.drawable.custom_img_bg)
                4 -> image4.setBackgroundResource(R.drawable.custom_img_bg)
                5 -> image5.setBackgroundResource(R.drawable.custom_img_bg)
                else -> Log.d("EditPartyInfoActivity","error")
            }
        }
    }
}