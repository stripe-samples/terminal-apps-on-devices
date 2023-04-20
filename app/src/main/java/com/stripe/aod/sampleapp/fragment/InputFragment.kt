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
import com.stripe.aod.sampleapp.utils.launchAndRepeatWithViewLifecycle

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
        viewBinding.keypad.keyboard0.setOnTouchListener(this)
        viewBinding.keypad.keyboard1.setOnTouchListener(this)
        viewBinding.keypad.keyboard2.setOnTouchListener(this)
        viewBinding.keypad.keyboard3.setOnTouchListener(this)
        viewBinding.keypad.keyboard4.setOnTouchListener(this)
        viewBinding.keypad.keyboard5.setOnTouchListener(this)
        viewBinding.keypad.keyboard6.setOnTouchListener(this)
        viewBinding.keypad.keyboard7.setOnTouchListener(this)
        viewBinding.keypad.keyboard8.setOnTouchListener(this)
        viewBinding.keypad.keyboard9.setOnTouchListener(this)
        viewBinding.keypad.keyboardClear.setOnTouchListener(this)
        viewBinding.keypad.keyboardBackspace.setOnTouchListener(this)

        inputViewModel.displayAmount(action = InputViewModel.ACTION.CLEAR)

        launchAndRepeatWithViewLifecycle {
            inputViewModel.showModifierKeys.collect {
                val visibility = if (it) View.VISIBLE else View.INVISIBLE

                viewBinding.keypad.keyboardClear.visibility = visibility
                viewBinding.keypad.keyboardBackspace.visibility = visibility
                viewBinding.submit.isEnabled = it
            }
        }

        launchAndRepeatWithViewLifecycle {
            inputViewModel.amt.collect {
                viewBinding.amount.text = it
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        var inputChar: Char? = null
        val scaleView = when (val id = view.id) {
            R.id.keyboard_0 -> {
                inputChar = '0'
                viewBinding.keypad.digit0
            }
            R.id.keyboard_1 -> {
                inputChar = '1'
                viewBinding.keypad.digit1
            }
            R.id.keyboard_2 -> {
                inputChar = '2'
                viewBinding.keypad.digit2
            }
            R.id.keyboard_3 -> {
                inputChar = '3'
                viewBinding.keypad.digit3
            }
            R.id.keyboard_4 -> {
                inputChar = '4'
                viewBinding.keypad.digit4
            }
            R.id.keyboard_5 -> {
                inputChar = '5'
                viewBinding.keypad.digit5
            }
            R.id.keyboard_6 -> {
                inputChar = '6'
                viewBinding.keypad.digit6
            }
            R.id.keyboard_7 -> {
                inputChar = '7'
                viewBinding.keypad.digit7
            }
            R.id.keyboard_8 -> {
                inputChar = '8'
                viewBinding.keypad.digit8
            }
            R.id.keyboard_9 -> {
                inputChar = '9'
                viewBinding.keypad.digit9
            }
            R.id.keyboard_clear -> {
                viewBinding.keypad.tvClear
            }
            R.id.keyboard_backspace -> {
                viewBinding.keypad.tvBackspace
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
            viewBinding.keypad.tvClear -> {
                inputViewModel.displayAmount(action = InputViewModel.ACTION.CLEAR)
            }
            viewBinding.keypad.tvBackspace -> {
                inputViewModel.displayAmount(action = InputViewModel.ACTION.DELETE)
            }
            else -> {
                inputViewModel.displayAmount(inputChar)
            }
        }
    }
}
