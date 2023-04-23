package com.stripe.aod.sampleapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentInputBinding
import com.stripe.aod.sampleapp.model.InputViewModel
import com.stripe.aod.sampleapp.utils.formatCentsToString
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle
import com.stripe.aod.sampleapp.utils.navOptions

class InputFragment : Fragment(R.layout.fragment_input), OnTouchListener {
    private lateinit var viewBinding: FragmentInputBinding
    private val inputViewModel by viewModels<InputViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        // hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView(view: View) {
        viewBinding = FragmentInputBinding.bind(view)
        viewBinding.back.setOnClickListener { findNavController().navigateUp() }
        viewBinding.keypad.key0.setOnTouchListener(this)
        viewBinding.keypad.key1.setOnTouchListener(this)
        viewBinding.keypad.key2.setOnTouchListener(this)
        viewBinding.keypad.key3.setOnTouchListener(this)
        viewBinding.keypad.key4.setOnTouchListener(this)
        viewBinding.keypad.key5.setOnTouchListener(this)
        viewBinding.keypad.key6.setOnTouchListener(this)
        viewBinding.keypad.key7.setOnTouchListener(this)
        viewBinding.keypad.key8.setOnTouchListener(this)
        viewBinding.keypad.key9.setOnTouchListener(this)
        viewBinding.keypad.keyClear.setOnTouchListener(this)
        viewBinding.keypad.keyBackspace.setOnTouchListener(this)
        viewBinding.submit.setOnClickListener { requestNewPayment() }

        inputViewModel.displayAmount(action = InputViewModel.Action.Clear)

        launchAndRepeatWithViewLifecycle {
            inputViewModel.showModifierKeys.collect {
                val visibility = if (it) View.VISIBLE else View.INVISIBLE

                viewBinding.keypad.keyClear.visibility = visibility
                viewBinding.keypad.keyBackspace.visibility = visibility
                viewBinding.submit.isEnabled = it
            }
        }

        launchAndRepeatWithViewLifecycle {
            inputViewModel.amount.collect {
                viewBinding.amount.text = if (it.isEmpty()) {
                    formatCentsToString(0)
                } else {
                    formatCentsToString(
                        it.toInt()
                    )
                }
            }
        }
    }

    private fun requestNewPayment() {
        findNavController().navigate(
            InputFragmentDirections.actionInputFragmentToCheckoutFragment(
                inputViewModel.amount.value.toInt()
            ),
            navOptions()
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        var inputChar: Char? = null
        val scaleView = when (val id = view.id) {
            R.id.key_0 -> {
                inputChar = '0'
                viewBinding.keypad.digit0
            }
            R.id.key_1 -> {
                inputChar = '1'
                viewBinding.keypad.digit1
            }
            R.id.key_2 -> {
                inputChar = '2'
                viewBinding.keypad.digit2
            }
            R.id.key_3 -> {
                inputChar = '3'
                viewBinding.keypad.digit3
            }
            R.id.key_4 -> {
                inputChar = '4'
                viewBinding.keypad.digit4
            }
            R.id.key_5 -> {
                inputChar = '5'
                viewBinding.keypad.digit5
            }
            R.id.key_6 -> {
                inputChar = '6'
                viewBinding.keypad.digit6
            }
            R.id.key_7 -> {
                inputChar = '7'
                viewBinding.keypad.digit7
            }
            R.id.key_8 -> {
                inputChar = '8'
                viewBinding.keypad.digit8
            }
            R.id.key_9 -> {
                inputChar = '9'
                viewBinding.keypad.digit9
            }
            R.id.key_clear -> {
                viewBinding.keypad.clear
            }
            R.id.key_backspace -> {
                viewBinding.keypad.backspace
            }
            else -> {
                error("Unexpected view with id: $id")
            }
        }

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleView.scaleX = 1.5.toFloat()
                scaleView.scaleY = 1.5.toFloat()
            }
            MotionEvent.ACTION_UP -> {
                scaleView.scaleX = 1f
                scaleView.scaleY = 1f
                handlerClickAction(scaleView, inputChar)
            }
        }
        return true
    }

    private fun handlerClickAction(view: View, inputChar: Char?) {
        when (view) {
            viewBinding.keypad.clear -> {
                inputViewModel.displayAmount(action = InputViewModel.Action.Clear)
            }
            viewBinding.keypad.backspace -> {
                inputViewModel.displayAmount(action = InputViewModel.Action.Delete)
            }
            else -> {
                inputViewModel.displayAmount(inputChar)
            }
        }
    }
}
