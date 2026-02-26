package com.unoharu.androidcalcapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale
import android.view.MotionEvent

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MAX_DIGITS = 9
        private const val MAX_DECIMAL_PLACES = 8
    }

    private lateinit var display: TextView
    private var input = "0"
    private var operand1: Double? = null
    private var operator: String? = null
    private var isResultDisplayed = false
    private val decimalFormat = NumberFormat.getInstance(Locale.US) as DecimalFormat
    private val scientificFormat = DecimalFormat("0.###E0")
    private var startX = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        display = findViewById(R.id.display)

        updateDisplay()

        val numberButtons = listOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.buttonDot
        )

        numberButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                if (isResultDisplayed) {
                    input = "0"
                    isResultDisplayed = false
                }
                val button = it as Button
                handleInput(button.text.toString())
            }
        }

        val operatorButtons = listOf(
            R.id.buttonPlus, R.id.buttonMinus, R.id.buttonMultiply, R.id.buttonDivide
        )

        operatorButtons.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                val button = it as Button
                onOperatorButtonClick(button.text.toString())
            }
        }

        findViewById<Button>(R.id.buttonEquals).setOnClickListener {
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

        findViewById<Button>(R.id.buttonAC).setOnClickListener {
            input = "0"
            operand1 = null
            operator = null
            isResultDisplayed = false
            updateDisplay()
        }

        findViewById<Button>(R.id.buttonPlusMinus).setOnClickListener {
            if (input.isNotEmpty()) {
                input = if (input.startsWith("-")) {
                    input.substring(1)
                } else {
                    "-$input"
                }
                updateDisplay()
            }
        }

        findViewById<Button>(R.id.buttonPercent).setOnClickListener {
            if (input.isNotEmpty()) {
                val value = input.replace(",", "").toDoubleOrNull()
                if (value != null) {
                    input = formatNumber(value / 100)
                    updateDisplay()
                }
            }
        }

        display.setOnTouchListener { _, event ->
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
            display.text = input
        } else {
            val displayValue = if (isResultDisplayed) input else formatNumber(input.toDoubleOrNull() ?: 0.0)
            display.text = displayValue
        }
    }
}
