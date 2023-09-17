package com.bapool.bapool.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
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
import com.google.android.material.chip.Chip
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
    val retro = ServerRetrofit.create()
    val userId: Long = UserId!!
    val TAG = "EditPartyInfoActivity"
    var maxpeopeleInt: Int = 0
    var receivePartyInfo: FirebasePartyInfo = FirebasePartyInfo()
    var partyId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPartyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeVari()
        listener()
        keyboard()
    }

    fun keyboard(){

            val scrollviewMain = binding.rootView
            binding.detail.setOnTouchListener { view, motionEvent ->
                scrollviewMain.requestDisallowInterceptTouchEvent(true)
                false
            }


    }


    fun initializeVari() {

        receivePartyInfo = intent.getSerializableExtra("partyInfo") as FirebasePartyInfo
        partyId = intent.getStringExtra("partyId").toString()
        binding.grpNameText.setText(receivePartyInfo.groupName)
        binding.menuText.setText(receivePartyInfo.menu)
        binding.detail.setText(receivePartyInfo.groupDetail)
        binding.maxPeopleText.setText(receivePartyInfo.maxNumberOfPeople.toString())
        maxpeopeleInt = receivePartyInfo.maxNumberOfPeople.toInt()
        changeDateFormat(binding.startDateText, binding.startTimeText)
        setHashtagInfo()


        binding.grpNameText.addTextChangedListener(object : TextWatcher {
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

        binding.menuText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed.
            }

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called whenever the text is changed.
                val charCount = charSequence?.length ?: 0
                if(charCount <= 10){
                    binding.menuEdit.text ="(${charCount}/10)"
                    binding.menuEdit.setTextColor(Color.parseColor("#5B5BFB"))
                }else if(charCount > 10){
                    binding.menuEdit.text ="(${charCount}/10)"
                    binding.menuEdit.setTextColor(Color.RED)
                }
            }

            override fun afterTextChanged(editable: Editable?) {
                // This method is called after the text is changed.
            }
        })
    }

    fun listener() {
        hashtagClickListener(binding.chip1,0)
        hashtagClickListener(binding.chip2,1)
        hashtagClickListener(binding.chip3,2)
        hashtagClickListener(binding.chip4,3)
        hashtagClickListener(binding.chip5,4)

        //모임시작 날짜 정하기
        binding.startDateConst.setOnClickListener {
            binding.startDateConst.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            datePickerDialogCustom(System.currentTimeMillis(), 1)
        }
        //모임 시작 시간 정하기
        binding.startTimeConst.setOnClickListener {
            binding.startTimeConst.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            timePickerDialogCustom(1)
        }
        //모임 정원 정하기
        binding.maxPeopleConst.setOnClickListener {
            binding.maxPeopleConst.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in))
            showMaxPeopleDialog()
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
            } else if(binding.maxPeopleText.text.toString().isNullOrBlank()){
                alterDialog("정원을 선택해주세요.")

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
                        maxpeopeleInt,
                        startDateLocal,
                        endDateLocal,
                        binding.menuText.text.toString(),
                        binding.detail.text.toString(),
                        hashtagList
                    )

                retrofit(editPartyInstance)
            }


        }
    }


    //hashtag 클릭 리스너
    fun hashtagClickListener(chip: Chip, i: Int) {
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                hastagList.set(i, 1)

            } else {
                hastagList.set(i, 0)
            }
        }
    }

    fun retrofit(editParty: PatchEditPartyInfoRequest) {

        retro.editParty(UserId!!.toLong(), editParty)
            .enqueue(object : Callback<PatchEditPartyInfoResponse> {
                override fun onResponse(
                    call: Call<PatchEditPartyInfoResponse>,
                    response: Response<PatchEditPartyInfoResponse>,
                ) {
                    var result: PatchEditPartyInfoResponse? = response.body()

                    if (response.isSuccessful) {
                        finish()
                    } else {
                        Toast.makeText(
                            this@EditPartyInfoActivity,
                            "파티" +
                                    " 생성 오류 fail",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                }

                override fun onFailure(call: Call<PatchEditPartyInfoResponse>, t: Throwable) {

                    Toast.makeText(this@EditPartyInfoActivity, "파티 생성 오류", Toast.LENGTH_SHORT)
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

    //모집인원 dialog
    fun showMaxPeopleDialog(){
        val num = resources.getStringArray(R.array.maxpeople)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("모임 인원 선택")
        builder.setItems(num){
                p0,p1 ->
            binding.maxPeopleText.setText(num[p1])
            maxpeopeleInt = num[p1].toInt()
        }
        val alertDialog = builder.create()
        alertDialog.show()

    }

    fun setHashtagInfo() {
        val receiveHashtag = receivePartyInfo.hashTag
        val chip1 = binding.chip1
        val chip2 = binding.chip2
        val chip3 = binding.chip3
        val chip4 = binding.chip4
        val chip5 = binding.chip5

        for (data in receiveHashtag) {
            when (data) {
                1 -> {
                    chip1.isChecked = true
                    hastagList.set(0, 1)
                }

                2 -> {
                    chip2.isChecked = true
                    hastagList.set(1, 1)
                }
                3 -> {
                    chip3.isChecked = true
                    hastagList.set(2, 1)

                }
                4 -> {
                    chip4.isChecked = true
                    hastagList.set(3, 1)

                }
                5 -> {
                    chip5.isChecked = true
                    hastagList.set(4, 1)

                }
                else -> Log.d("EditPartyInfoActivity", "error")

            }

        }
    }
}