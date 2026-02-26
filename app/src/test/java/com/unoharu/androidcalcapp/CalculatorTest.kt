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
}
