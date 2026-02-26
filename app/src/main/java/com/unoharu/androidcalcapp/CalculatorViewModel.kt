package com.unoharu.androidcalcapp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CalculatorUiState(
    val displayText: String = "0",
    val errorMessage: String? = null,
)

class CalculatorViewModel : ViewModel() {

    private val calculator = Calculator()

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onDigitClick(digit: String) {
        calculator.inputDigit(digit)
        updateUiState()
    }

    fun onDecimalClick() {
        calculator.inputDecimal()
        updateUiState()
    }

    fun onOperatorClick(symbol: String) {
        calculator.inputOperator(symbol)
        updateUiState()
    }

    fun onEqualsClick() {
        when (val result = calculator.calculate()) {
            is CalculationResult.Error -> {
                val message = when (result.type) {
                    ErrorType.DIVIDE_BY_ZERO -> ErrorType.DIVIDE_BY_ZERO.name
                }
                _uiState.value = CalculatorUiState(
                    displayText = calculator.displayText,
                    errorMessage = message,
                )
            }
            is CalculationResult.Success -> updateUiState()
            null -> {} // no-op when preconditions not met
        }
    }

    fun onClearClick() {
        calculator.clear()
        updateUiState()
    }

    fun onToggleSignClick() {
        calculator.toggleSign()
        updateUiState()
    }

    fun onPercentClick() {
        calculator.percent()
        updateUiState()
    }

    fun onBackspaceClick() {
        calculator.backspace()
        updateUiState()
    }

    fun onErrorDismissed() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    private fun updateUiState() {
        _uiState.value = CalculatorUiState(displayText = calculator.displayText)
    }
}
