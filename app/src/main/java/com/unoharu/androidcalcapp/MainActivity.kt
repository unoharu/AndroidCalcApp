package com.unoharu.androidcalcapp

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.unoharu.androidcalcapp.databinding.ActivityMainBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAX_DIGITS = 9
        private const val MAX_DECIMAL_PLACES = 8
    }

    private lateinit var binding: ActivityMainBinding
    private var input = "0"
    private var operand1: Double? = null
    private var operator: String? = null
    private var isResultDisplayed = false
    private val decimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
    private val scientificFormat = DecimalFormat("0.###E0")
    private var startX = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateDisplay()

        val numberButtons = listOf(
            binding.button0, binding.button1, binding.button2, binding.button3,
            binding.button4, binding.button5, binding.button6, binding.button7,
            binding.button8, binding.button9, binding.buttonDot
        )

        numberButtons.forEach { button ->
            button.setOnClickListener {
                if (isResultDisplayed) {
                    input = "0"
                    isResultDisplayed = false
                }
                handleInput((it as Button).text.toString())
            }
        }

        val operatorButtons = listOf(
            binding.buttonPlus, binding.buttonMinus, binding.buttonMultiply, binding.buttonDivide
        )

        operatorButtons.forEach { button ->
            button.setOnClickListener {
                onOperatorButtonClick((it as Button).text.toString())
            }
        }

        binding.buttonEquals.setOnClickListener {
            if (input.isNotEmpty() && operand1 != null && operator != null) {
                val operand2 = input.replace(",", "").toDoubleOrNull()
                if (operand2 != null) {
                    val result = when (operator) {
                        "+" -> operand1!! + operand2
                        "-" -> operand1!! - operand2
                        "×" -> operand1!! * operand2
                        "÷" -> if (operand2 != 0.0) operand1!! / operand2 else Double.NaN
                        else -> null
                    }

                    result?.let {
                        input = formatNumber(it)
                        isResultDisplayed = true
                        updateDisplay()
                    }
                }
            }
        }

        binding.buttonAC.setOnClickListener {
            input = "0"
            operand1 = null
            operator = null
            isResultDisplayed = false
            updateDisplay()
        }

        binding.buttonPlusMinus.setOnClickListener {
            if (input.isNotEmpty()) {
                input = if (input.startsWith("-")) {
                    input.substring(1)
                } else {
                    "-$input"
                }
                updateDisplay()
            }
        }

        binding.buttonPercent.setOnClickListener {
            if (input.isNotEmpty()) {
                val value = input.replace(",", "").toDoubleOrNull()
                if (value != null) {
                    input = formatNumber(value / 100)
                    updateDisplay()
                }
            }
        }

        binding.display.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val endX = event.x
                    if (startX > endX) {
                        removeLastDigit()
                    } else if (startX < endX) {
                        removeLastDigit()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun removeLastDigit() {
        if (input.isNotEmpty() && input != "0") {
            input = if (input.length > 1) {
                input.dropLast(1).takeIf { it.isNotEmpty() } ?: "0"
            } else {
                "0"
            }
            updateDisplay()
        }
    }

    private fun handleInput(buttonText: String) {
        if (buttonText == ".") {
            if (input == "0" || input.isEmpty()) {
                input = "0."
            } else if (!input.contains(".")) {
                input += buttonText
            }
        } else {
            val parts = input.split(".")
            val integerPart = parts[0]
            val decimalPart = if (parts.size > 1) parts[1] else ""

            val totalLength = integerPart.length + decimalPart.length

            if (totalLength < MAX_DIGITS || (parts.size > 1 && decimalPart.length < MAX_DECIMAL_PLACES)) {
                if (input == "0" && buttonText != ".") {
                    input = buttonText
                } else {
                    input += buttonText
                }
            }
        }
        updateDisplay()
    }

    private fun formatNumber(value: Double): String {
        return if (value >= 1000000000 || value <= -1000000000) {
            scientificFormat.format(value)
        } else {
            decimalFormat.maximumFractionDigits = MAX_DECIMAL_PLACES
            decimalFormat.format(value)
        }
    }

    private fun onOperatorButtonClick(operatorText: String) {
        if (input.isNotEmpty() && input != "." && !input.startsWith("0") && !input.startsWith("0.") || input.startsWith("0.") && input.length > 2) {
            operand1 = input.replace(",", "").toDoubleOrNull()
            operator = operatorText
            input = ""
            isResultDisplayed = false
        }
    }

    private fun updateDisplay() {
        if (input == "0.") {
            binding.display.text = input
        } else {
            val displayValue = if (isResultDisplayed) input else formatNumber(input.toDoubleOrNull() ?: 0.0)
            binding.display.text = displayValue
        }
    }
}
