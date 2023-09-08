package com.bapool.bapool.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityMakePartyBinding
import com.bapool.bapool.databinding.CustomDatepickerBinding
import com.bapool.bapool.databinding.CustomTimepickerBinding
import com.bapool.bapool.retrofit.ServerRetrofit
import com.bapool.bapool.retrofit.data.PostMakePartyRequest
import com.bapool.bapool.retrofit.data.PostMakePartyResponse
import com.bapool.bapool.retrofit.data.goToRestaurantPartyList
import com.bapool.bapool.ui.LoginActivity.Companion.UserId
import com.bapool.bapool.ui.RestaurantPartyActivity.Companion.RestaurantPartyActivityCompanion
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MakePartyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakePartyBinding
    val hastagList = ArrayList(Collections.nCopies(5, 0))
    val retro = ServerRetrofit.create()
    val userId: String = UserId.toString()   //companion userid로 변경필요.
    lateinit var restaurantPartyInfoObject: goToRestaurantPartyList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakePartyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        restaurantPartyInfoObject =
            intent.getSerializableExtra("restaurantInfoObject") as goToRestaurantPartyList

        initializeVari()
        listener()

    }


    fun initializeVari() {

        binding.resName.setText(this.restaurantPartyInfoObject.name)

        binding.grpNameText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val charCount = charSequence?.length ?: 0
                if(charCount <= 10){
                    binding.partyNameEdit.text ="(${charCount}/10)"
                    binding.partyNameEdit.setTextColor(Color.parseColor("#5B5BFB"))
                }else if(charCount > 10){
                    binding.partyNameEdit.text ="(${charCount}/10)"
                    binding.partyNameEdit.setTextColor(Color.RED)
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called after the text is changed.
            }
        })

        binding.menuText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val charCount = charSequence?.length ?: 0
                if(charCount <= 10){
                    binding.menuEdit.text ="(${charCount} / 10)"
                    binding.menuEdit.setTextColor(Color.parseColor("#5B5BFB"))
                }else if(charCount > 10){
                    binding.menuEdit.text ="(${charCount} / 10)"
                    binding.menuEdit.setTextColor(Color.RED)
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called after the text is changed.
            }
        })
    }

    fun listener() {
        //category
        binding.chip1.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(0, 1)
                Log.d("chip1",hastagList.toString())

            } else {
                hastagList.set(0, 0)
                Log.d("chip1",hastagList.toString())

            }
        }
        binding.chip2.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(1, 1)
                Log.d("chip1",hastagList.toString())


            } else {
                hastagList.set(1, 0)
                Log.d("chip1",hastagList.toString())

            }
        }
        binding.chip3.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(2, 1)
                Log.d("chip1",hastagList.toString())

            } else {

                hastagList.set(2, 0)
                Log.d("chip1",hastagList.toString())

            }
        }
        binding.chip4.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(3, 1)
                Log.d("chip1",hastagList.toString())

            } else {
                hastagList.set(3, 0)
                Log.d("chip1",hastagList.toString())

            }
        }
        binding.chip5.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(4, 1)
                Log.d("chip1",hastagList.toString())

            } else {
                hastagList.set(4, 0)
                Log.d("chip1",hastagList.toString())
            }
        }


        //모임시작 날짜 정하기
        binding.startDateConst.setOnClickListener {
            datePickerDialogCustom(System.currentTimeMillis(), 1)
        }
        //모임 시작 시간 정하기
        binding.startTimeConst.setOnClickListener {
            binding.startTimeConst.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            timePickerDialogCustom(1)
        }


        //그룹생성버튼, 그룹생성정보를 retrofit post로 넘겨줌
        binding.makeGrpButton.setOnClickListener {


            if (binding.grpNameText.text.isNullOrBlank()) {
                alterDialog("파티명을 입력해주세요.")

            } else if (binding.grpNameText.length() > 10) {
                alterDialog("파티명은 10글자까지 가능합니다.")

            } else if (binding.menuText.text.isNullOrBlank()) {
                alterDialog("메뉴를 입력해주세요.")

            } else if (binding.menuText.length() > 10) {
                alterDialog("메뉴명은 10글자까지 가능합니다.")

            } else if (binding.startDateText.text.toString().isNullOrBlank()) {
                alterDialog("시작날짜를 입력해주세요.")

            } else if (binding.startTimeText.text.toString().isNullOrBlank()) {
                alterDialog("시작시간을 입력해주세요.")
            } else {
                val endDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"
                val startDateLocal =
                    binding.startDateText.text.toString() + " " + binding.startTimeText.text.toString() + ":00"

                var hashtagList = arrayListOf<Int>()
                var count = 0
                for (data in hastagList) {
                    count++
                    if (data != 0) {
                        when (count) {
                            1 -> hashtagList.add(1)
                            2 -> hashtagList.add(2)
                            3 -> hashtagList.add(3)
                            4 -> hashtagList.add(4)
                            5 -> hashtagList.add(5)
                        }
                    }


                }


                val makeGrpInstance =
                    PostMakePartyRequest(
                        binding.grpNameText.text.toString(),
                        2,
                        startDateLocal,
                        endDateLocal,
                        binding.menuText.text.toString(),
                        hashtagList,
                        binding.detail.text.toString(),
                        restaurantPartyInfoObject
                    )

                retrofit(makeGrpInstance)
            }


        }
    }


    fun retrofit(makeParty: PostMakePartyRequest) {


        retro.makeParty(userId.toLong(), makeParty)
            .enqueue(object : Callback<PostMakePartyResponse> {
                override fun onResponse(
                    call: Call<PostMakePartyResponse>,
                    response: Response<PostMakePartyResponse>,
                ) {

                    if (response.isSuccessful) {
                        var result: PostMakePartyResponse? =
                            response.body()
                        val intent =
                            Intent(
                                this@MakePartyActivity,
                                ChattingAndPartyInfoMFActivity::class.java
                            )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                        intent.putExtra("whereAreYouFrom", "make")
                        intent.putExtra("restaurantInfoObject", restaurantPartyInfoObject)
                        intent.putExtra("partyId",
                            result!!.result.party_id.toString()) // result 안의 party_id 값으로 value값 교체
                        startActivity(intent)

                        val active = mapOf<String, String>(userId to "active")
                        FirebaseDatabase.getInstance().getReference("test").child("InTheParty")
                            .child(result!!.result.party_id.toString())
                            .setValue(active)

                        RestaurantPartyActivityCompanion?.finish()
                        finish()
                    } else {
                        Toast.makeText(this@MakePartyActivity, "파티 생성 오류", Toast.LENGTH_SHORT)
                            .show()
                    }
                }


                override fun onFailure(call: Call<PostMakePartyResponse>, t: Throwable) {
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

    fun chageTimeForm(startTimeUnit: String, endTimeUnit: String): Int {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.KOREA)
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()

        calendar1.time = timeFormat.parse(startTimeUnit)
        calendar2.time = timeFormat.parse(endTimeUnit)

        val cmp = calendar1.compareTo(calendar2)
        return cmp

    }
}
