package com.unoharu.androidcalcapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorTest {

    private lateinit var calculator: Calculator

    @Before
    fun setUp() {
        calculator = Calculator()
    }

    // --- Basic arithmetic ---

    @Test
    fun addition() {
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("8", calculator.displayText)
    }

    @Test
    fun subtraction() {
        calculator.inputDigit("9")
        calculator.inputOperator("-")
        calculator.inputDigit("4")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("5", calculator.displayText)
    }

    @Test
    fun multiplication() {
        calculator.inputDigit("6")
        calculator.inputOperator("×")
        calculator.inputDigit("7")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("42", calculator.displayText)
    }

    @Test
    fun division() {
        calculator.inputDigit("8")
        calculator.inputOperator("÷")
        calculator.inputDigit("2")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("4", calculator.displayText)
    }

    // --- Bug 1: "0" as first operand ---

    @Test
    fun zeroAsFirstOperand() {
        // input starts as "0", so just press operator directly
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("5", calculator.displayText)
    }

    @Test
    fun zeroAsFirstOperandMultiply() {
        calculator.inputOperator("×")
        calculator.inputDigit("9")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("0", calculator.displayText)
    }

    // --- Bug 2: divide by zero ---

    @Test
    fun divideByZeroReturnsError() {
        calculator.inputDigit("5")
        calculator.inputOperator("÷")
        // second operand defaults to "0" after operator clears input, so type "0"
        calculator.inputDigit("0")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Error)
        assertEquals(ErrorType.DIVIDE_BY_ZERO, (result as CalculationResult.Error).type)
    }

    @Test
    fun divideByZeroResetsState() {
        calculator.inputDigit("5")
        calculator.inputOperator("÷")
        calculator.inputDigit("0")
        calculator.calculate()
        // State should be reset after error
        assertEquals("0", calculator.displayText)
        assertNull(calculator.state.operand1)
        assertNull(calculator.state.operator)
    }

    // --- Decimal input ---

    @Test
    fun decimalInput() {
        calculator.inputDigit("1")
        calculator.inputDecimal()
        calculator.inputDigit("5")
        assertEquals("1.5", calculator.displayText)
    }

    @Test
    fun decimalInputFromZero() {
        calculator.inputDecimal()
        calculator.inputDigit("5")
        assertEquals("0.5", calculator.displayText)
    }

    @Test
    fun duplicateDecimalIgnored() {
        calculator.inputDigit("1")
        calculator.inputDecimal()
        calculator.inputDecimal()
        calculator.inputDigit("2")
        assertEquals("1.2", calculator.displayText)
    }

    // --- Percent ---

    @Test
    fun percent() {
        calculator.inputDigit("5")
        calculator.inputDigit("0")
        calculator.percent()
        assertEquals("0.5", calculator.displayText)
    }

    // --- Toggle sign ---

    @Test
    fun toggleSign() {
        calculator.inputDigit("5")
        calculator.toggleSign()
        assertEquals("-5", calculator.displayText)
    }

    @Test
    fun toggleSignTwice() {
        calculator.inputDigit("5")
        calculator.toggleSign()
        calculator.toggleSign()
        assertEquals("5", calculator.displayText)
    }

    @Test
    fun toggleSignOnZeroDoesNothing() {
        calculator.toggleSign()
        assertEquals("0", calculator.displayText)
    }

    // --- Backspace ---

    @Test
    fun backspaceSingleDigitResetsToZero() {
        calculator.inputDigit("5")
        calculator.backspace()
        assertEquals("0", calculator.displayText)
    }

    @Test
    fun backspaceMultipleDigits() {
        calculator.inputDigit("1")
        calculator.inputDigit("2")
        calculator.inputDigit("3")
        calculator.backspace()
        assertEquals("12", calculator.displayText)
    }

    @Test
    fun backspaceOnZeroDoesNothing() {
        calculator.backspace()
        assertEquals("0", calculator.displayText)
    }

    // --- Max digits ---

    @Test
    fun maxNineDigits() {
        repeat(9) { calculator.inputDigit("1") }
        calculator.inputDigit("2") // 10th digit should be ignored
        assertEquals("111,111,111", calculator.displayText)
    }

    // --- Clear ---

    @Test
    fun clearResetsAll() {
        calculator.inputDigit("5")
        calculator.inputOperator("+")
        calculator.inputDigit("3")
        calculator.clear()
        assertEquals("0", calculator.displayText)
        assertNull(calculator.state.operand1)
        assertNull(calculator.state.operator)
        assertEquals(false, calculator.state.isResultDisplayed)
    }

    // --- Result then new input ---

    @Test
    fun newInputAfterResultClearsPrevious() {
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        calculator.calculate()
        assertEquals("8", calculator.displayText)

        calculator.inputDigit("2")
        assertEquals("2", calculator.displayText)
    }

    // --- Calculate without operator returns null ---

    @Test
    fun calculateWithoutOperatorReturnsNull() {
        calculator.inputDigit("5")
        val result = calculator.calculate()
        assertNull(result)
    }

    // --- Multi-digit operands ---

    @Test
    fun multiDigitAddition() {
        calculator.inputDigit("1")
        calculator.inputDigit("2")
        calculator.inputOperator("+")
        calculator.inputDigit("3")
        calculator.inputDigit("4")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("46", calculator.displayText)
    }

    // --- Decimal arithmetic ---

    @Test
    fun decimalArithmetic() {
        calculator.inputDigit("1")
        calculator.inputDecimal()
        calculator.inputDigit("5")
        calculator.inputOperator("+")
        calculator.inputDigit("2")
        calculator.inputDecimal()
        calculator.inputDigit("3")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("3.8", calculator.displayText)
    }

    // --- Chained calculation ---

    @Test
    fun chainedAddition() {
        // 3 + 5 + 2 = should produce 10
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        calculator.inputOperator("+") // triggers 3+5=8 internally
        calculator.inputDigit("2")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("10", calculator.displayText)
    }

    @Test
    fun chainedMixedOperators() {
        // 10 - 3 × 2 = should produce 14 (left-to-right evaluation)
        calculator.inputDigit("1")
        calculator.inputDigit("0")
        calculator.inputOperator("-")
        calculator.inputDigit("3")
        calculator.inputOperator("×") // triggers 10-3=7 internally
        calculator.inputDigit("2")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("14", calculator.displayText)
    }

    @Test
    fun chainedThreeOperators() {
        // 1 + 2 + 3 + 4 = should produce 10
        calculator.inputDigit("1")
        calculator.inputOperator("+")
        calculator.inputDigit("2")
        calculator.inputOperator("+") // 1+2=3
        calculator.inputDigit("3")
        calculator.inputOperator("+") // 3+3=6
        calculator.inputDigit("4")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("10", calculator.displayText)
    }

    // --- History ---

    @Test
    fun historyAddedAfterCalculation() {
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        calculator.calculate()

        assertEquals(1, calculator.history.size)
        assertEquals("3 + 5", calculator.history[0].expression)
        assertEquals("8", calculator.history[0].result)
    }

    @Test
    fun historyAddedForChainedCalculation() {
        // 3 + 5 + 2 = produces two history entries (intermediate + final)
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        calculator.inputOperator("+") // intermediate: 3+5=8
        calculator.inputDigit("2")
        calculator.calculate() // final: 8+2=10

        assertEquals(2, calculator.history.size)
        assertEquals("3 + 5", calculator.history[0].expression)
        assertEquals("8", calculator.history[0].result)
        assertEquals("8 + 2", calculator.history[1].expression)
        assertEquals("10", calculator.history[1].result)
    }

    @Test
    fun historyMaxSize() {
        // Exceed max history size (20) and verify oldest entries are removed
        repeat(25) { i ->
            calculator.inputDigit("${i % 10}")
            calculator.inputOperator("+")
            calculator.inputDigit("1")
            calculator.calculate()
        }

        assertEquals(20, calculator.history.size)
    }

    @Test
    fun historyClearedOnClear() {
        calculator.inputDigit("3")
        calculator.inputOperator("+")
        calculator.inputDigit("5")
        calculator.calculate()
        assertEquals(1, calculator.history.size)

        calculator.clear()
        assertEquals(0, calculator.history.size)
    }

    @Test
    fun historyNotAddedOnError() {
        calculator.inputDigit("5")
        calculator.inputOperator("÷")
        calculator.inputDigit("0")
        calculator.calculate()

        assertEquals(0, calculator.history.size)
    }

    // --- setInput ---

    @Test
    fun setInputSetsCurrentInput() {
        calculator.setInput("42")
        assertEquals("42", calculator.displayText)
        assertTrue(calculator.state.isResultDisplayed)
    }

    @Test
    fun setInputAllowsNewCalculation() {
        calculator.setInput("42")
        calculator.inputOperator("+")
        calculator.inputDigit("8")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("50", calculator.displayText)
    }

    // --- Trailing zeros in decimal display ---

    @Test
    fun displayTrailingZerosInDecimal() {
        calculator.inputDecimal()
        calculator.inputDigit("0")
        assertEquals("0.0", calculator.displayText)
    }

    @Test
    fun displayMultipleTrailingZeros() {
        calculator.inputDecimal()
        calculator.inputDigit("0")
        calculator.inputDigit("0")
        assertEquals("0.00", calculator.displayText)
    }

    @Test
    fun displayTrailingZeroAfterNonZero() {
        calculator.inputDigit("1")
        calculator.inputDecimal()
        calculator.inputDigit("5")
        calculator.inputDigit("0")
        assertEquals("1.50", calculator.displayText)
    }

    // --- Large numbers and scientific notation ---

    @Test
    fun largeNumberUsesScientificNotation() {
        // 500,000 × 500,000 = 250,000,000,000 (>= 1e9 threshold)
        calculator.inputDigit("5")
        repeat(5) { calculator.inputDigit("0") }
        calculator.inputOperator("×")
        calculator.inputDigit("5")
        repeat(5) { calculator.inputDigit("0") }
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("2.5E11", calculator.displayText)
    }

    @Test
    fun scientificNotationResultUsableViaSetInput() {
        // Simulate tapping a scientific notation history result
        calculator.setInput("2.5E11")
        calculator.inputOperator("+")
        calculator.inputDigit("1")
        val result = calculator.calculate()
        assertTrue(result is CalculationResult.Success)
        assertEquals("2.5E11", calculator.displayText)
    }

    // --- Backspace on formatted number ---

    @Test
    fun backspaceOnFormattedMultiDigitNumber() {
        // Input 1234 (displays as "1,234"), backspace should produce "123"
        calculator.inputDigit("1")
        calculator.inputDigit("2")
        calculator.inputDigit("3")
        calculator.inputDigit("4")
        assertEquals("1,234", calculator.displayText)
        calculator.backspace()
        assertEquals("123", calculator.displayText)
    }
}
