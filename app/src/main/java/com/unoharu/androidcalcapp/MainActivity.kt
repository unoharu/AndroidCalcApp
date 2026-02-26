package com.unoharu.androidcalcapp

import android.os.Bundle
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.unoharu.androidcalcapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val MIN_SWIPE_DISTANCE = 50f
    }

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CalculatorViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private var startX = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHistoryList()
        observeUiState()
        setupNumberButtons()
        setupOperatorButtons()
        setupFunctionButtons()
        setupSwipeGesture()
    }

    private fun setupHistoryList() {
        historyAdapter = HistoryAdapter { item ->
            viewModel.onHistoryItemClick(item)
        }
        binding.historyList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = historyAdapter
        }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.display.text = state.displayText

                    historyAdapter.submitList(state.history) {
                        // Auto-scroll to the latest history item
                        if (state.history.isNotEmpty()) {
                            binding.historyList.scrollToPosition(state.history.size - 1)
                        }
                    }

                    state.errorMessage?.let { errorKey ->
                        val message = when (errorKey) {
                            ErrorType.DIVIDE_BY_ZERO.name -> getString(R.string.error_divide_by_zero)
                            else -> getString(R.string.error_invalid_input)
                        }
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                        viewModel.onErrorDismissed()
                    }
                }
            }
        }
    }

    private fun setupNumberButtons() {
        val digitButtons = mapOf(
            binding.button0 to "0", binding.button1 to "1", binding.button2 to "2",
            binding.button3 to "3", binding.button4 to "4", binding.button5 to "5",
            binding.button6 to "6", binding.button7 to "7", binding.button8 to "8",
            binding.button9 to "9",
        )

        digitButtons.forEach { (button, digit) ->
            button.setOnClickListener { viewModel.onDigitClick(digit) }
        }

        binding.buttonDot.setOnClickListener { viewModel.onDecimalClick() }
    }

    private fun setupOperatorButtons() {
        binding.buttonPlus.setOnClickListener { viewModel.onOperatorClick("+") }
        binding.buttonMinus.setOnClickListener { viewModel.onOperatorClick("-") }
        binding.buttonMultiply.setOnClickListener { viewModel.onOperatorClick("×") }
        binding.buttonDivide.setOnClickListener { viewModel.onOperatorClick("÷") }
        binding.buttonEquals.setOnClickListener { viewModel.onEqualsClick() }
    }

    private fun setupFunctionButtons() {
        binding.buttonAC.setOnClickListener { viewModel.onClearClick() }
        binding.buttonPlusMinus.setOnClickListener { viewModel.onToggleSignClick() }
        binding.buttonPercent.setOnClickListener { viewModel.onPercentClick() }
        binding.buttonDelete.setOnClickListener { viewModel.onBackspaceClick() }
    }

    @Suppress("ClickableViewAccessibility")
    private fun setupSwipeGesture() {
        binding.display.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = event.x
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val deltaX = event.x - startX
                    // Bug fix: only left swipe triggers backspace
                    if (deltaX < -MIN_SWIPE_DISTANCE) {
                        viewModel.onBackspaceClick()
                    }
                    true
                }
                else -> false
            }
        }
    }
}
