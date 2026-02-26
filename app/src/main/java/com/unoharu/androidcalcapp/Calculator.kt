package com.unoharu.androidcalcapp

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

enum class Operator(val symbol: String) {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("×"),
    DIVIDE("÷");

    companion object {
        fun fromSymbol(symbol: String): Operator? = entries.find { it.symbol == symbol }
    }
}

data class CalculatorState(
    val currentInput: String = "0",
    val operand1: Double? = null,
    val operator: Operator? = null,
    val isResultDisplayed: Boolean = false,
)

sealed class CalculationResult {
    data class Success(val value: Double) : CalculationResult()
    data class Error(val type: ErrorType) : CalculationResult()
}

enum class ErrorType {
    DIVIDE_BY_ZERO,
}

data class HistoryItem(val expression: String, val result: String)

class Calculator {

    companion object {
        const val MAX_DIGITS = 9
        private const val MAX_DECIMAL_PLACES = 8
        private const val SCIENTIFIC_THRESHOLD = 1_000_000_000.0
        private const val MAX_HISTORY_SIZE = 20
    }

    private val decimalFormat = (NumberFormat.getInstance(Locale.US) as DecimalFormat).apply {
        maximumFractionDigits = MAX_DECIMAL_PLACES
    }
    private val scientificFormat = DecimalFormat("0.###E0")

    var state = CalculatorState()
        private set

    private val _history = mutableListOf<HistoryItem>()
    val history: List<HistoryItem> get() = _history.toList()

    val displayText: String
        get() = formatForDisplay()

    fun inputDigit(digit: String) {
        var input = state.currentInput

        if (state.isResultDisplayed) {
            input = "0"
            state = state.copy(isResultDisplayed = false)
        }

        input = appendDigit(input, digit)
        state = state.copy(currentInput = input)
    }

    fun inputDecimal() {
        var input = state.currentInput

        if (state.isResultDisplayed) {
            input = "0"
            state = state.copy(isResultDisplayed = false)
        }

        input = when {
            input == "0" || input.isEmpty() -> "0."
            !input.contains(".") -> "$input."
            else -> input
        }
        state = state.copy(currentInput = input)
    }

    fun inputOperator(symbol: String) {
        val op = Operator.fromSymbol(symbol) ?: return

        // Chained calculation: if a pending operation exists, evaluate it first
        if (state.operand1 != null && state.operator != null && state.currentInput.isNotEmpty()) {
            val result = calculate() ?: return
            if (result is CalculationResult.Error) return
        }

        val parsed = state.currentInput.replace(",", "").toDoubleOrNull() ?: return

        state = state.copy(
            operand1 = parsed,
            operator = op,
            currentInput = "",
            isResultDisplayed = false,
        )
    }

    fun calculate(): CalculationResult? {
        val op1 = state.operand1 ?: return null
        val op = state.operator ?: return null
        val op2 = state.currentInput.replace(",", "").toDoubleOrNull() ?: return null

        val result = when (op) {
            Operator.ADD -> CalculationResult.Success(op1 + op2)
            Operator.SUBTRACT -> CalculationResult.Success(op1 - op2)
            Operator.MULTIPLY -> CalculationResult.Success(op1 * op2)
            Operator.DIVIDE -> {
                if (op2 == 0.0) {
                    CalculationResult.Error(ErrorType.DIVIDE_BY_ZERO)
                } else {
                    CalculationResult.Success(op1 / op2)
                }
            }
        }

        when (result) {
            is CalculationResult.Success -> {
                val formattedResult = formatNumber(result.value)
                _history.add(
                    HistoryItem(
                        expression = "${formatNumber(op1)} ${op.symbol} ${formatNumber(op2)}",
                        result = formattedResult,
                    )
                )
                if (_history.size > MAX_HISTORY_SIZE) {
                    _history.removeFirst()
                }
                state = state.copy(
                    currentInput = formattedResult,
                    operand1 = null,
                    operator = null,
                    isResultDisplayed = true,
                )
            }
            is CalculationResult.Error -> {
                state = CalculatorState()
            }
        }

        return result
    }

    fun clear() {
        state = CalculatorState()
        _history.clear()
    }

    fun setInput(value: String) {
        state = state.copy(
            currentInput = value,
            isResultDisplayed = true,
        )
    }

    fun toggleSign() {
        val input = state.currentInput
        if (input.isEmpty() || input == "0") return

        state = state.copy(
            currentInput = if (input.startsWith("-")) input.substring(1) else "-$input"
        )
    }

    fun percent() {
        val value = state.currentInput.replace(",", "").toDoubleOrNull() ?: return
        state = state.copy(
            currentInput = formatNumber(value / 100)
        )
    }

    fun backspace() {
        val input = state.currentInput
        if (input.isEmpty() || input == "0") return

        state = state.copy(
            currentInput = if (input.length > 1) {
                input.dropLast(1).takeIf { it.isNotEmpty() && it != "-" } ?: "0"
            } else {
                "0"
            }
        )
    }

    fun formatNumber(value: Double): String {
        return if (value >= SCIENTIFIC_THRESHOLD || value <= -SCIENTIFIC_THRESHOLD) {
            scientificFormat.format(value)
        } else {
            decimalFormat.format(value)
        }
    }

    private fun formatForDisplay(): String {
        val input = state.currentInput
        if (input == "0.") return input
        if (input.endsWith(".")) return input

        return if (state.isResultDisplayed) {
            input
        } else {
            formatNumber(input.toDoubleOrNull() ?: 0.0)
        }
    }

    private fun appendDigit(currentInput: String, digit: String): String {
        val parts = currentInput.split(".")
        val integerPart = parts[0]
        val decimalPart = if (parts.size > 1) parts[1] else ""
        val totalLength = integerPart.replace("-", "").length + decimalPart.length

        if (totalLength >= MAX_DIGITS) return currentInput

        return if (currentInput == "0") digit else currentInput + digit
    }
}
