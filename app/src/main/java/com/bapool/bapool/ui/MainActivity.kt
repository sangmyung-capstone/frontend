package com.bapool.bapool.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bapool.bapool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class MainActivity : AppCompatActivity() {

    class ModalBottomSheet : BottomSheetDialogFragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? = inflater.inflate(R.layout.fragment_bottomsheet, container, false)

        companion object {
            const val TAG = "ModalBottomSheet"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val modalBottomSheet = ModalBottomSheet()
        modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)
    }
}