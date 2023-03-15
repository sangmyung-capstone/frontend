package com.bapool.bapool.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bapool.bapool.R
import com.bapool.bapool.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    var selectedButton: Button? = null // initially no button is selected
    override fun onCreate(savedInstanceState: Bundle?) {
        var count = 0
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root);

        val buttons = listOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6
        )

        for (button in buttons) {
            button.setOnClickListener {
                selectButton(button, buttons)
                count = button.lineCount
            }
        }
    }

    private fun selectButton(selectedButton: Button, buttons: List<Button>) {
        for (button in buttons) {
            if (button == selectedButton) {
                button.setBackgroundResource(R.drawable.button_border)
            } else {
                button.setBackgroundResource(R.drawable.button_border_blue)
            }
        }
    }
}
