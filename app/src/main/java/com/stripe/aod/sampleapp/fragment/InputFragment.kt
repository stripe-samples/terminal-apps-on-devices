package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentInputBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.formatAmount
import com.stripe.aod.sampleapp.utils.navigateToTarget
import java.util.regex.Pattern

class InputFragment : Fragment(R.layout.fragment_input), OnTouchListener {
    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.InputFragment"
    }

    private var amt: String = ""
    private var tvAmount: TextView? = null
    private val pattern = Pattern.compile("[0]*")
    private var _viewBinding : FragmentInputBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity, object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun initView(view: View) {
        _viewBinding = FragmentInputBinding.bind(view)
        viewBinding.tvSubmit.setOnClickListener { createNewPayment(amt) }
        viewBinding.rlBack.setOnClickListener { activity?.backToPrevious() }
        viewBinding.keypad.numkey0.setOnClickListener { displayAmount(addAmountCharacter('0')) }
        viewBinding.keypad.numkey0.setOnTouchListener(this)
        viewBinding.keypad.numkey1.setOnClickListener{ displayAmount(addAmountCharacter('1')) }
        viewBinding.keypad.numkey1.setOnTouchListener(this)
        viewBinding.keypad.numkey2.setOnClickListener{ displayAmount(addAmountCharacter('2')) }
        viewBinding.keypad.numkey2.setOnTouchListener(this)
        viewBinding.keypad.numkey3.setOnClickListener{ displayAmount(addAmountCharacter('3')) }
        viewBinding.keypad.numkey3.setOnTouchListener(this)
        viewBinding.keypad.numkey4.setOnClickListener{ displayAmount(addAmountCharacter('4')) }
        viewBinding.keypad.numkey4.setOnTouchListener(this)
        viewBinding.keypad.numkey5.setOnClickListener{ displayAmount(addAmountCharacter('5')) }
        viewBinding.keypad.numkey5.setOnTouchListener(this)
        viewBinding.keypad.numkey6.setOnClickListener{ displayAmount(addAmountCharacter('6')) }
        viewBinding.keypad.numkey6.setOnTouchListener(this)
        viewBinding.keypad.numkey7.setOnClickListener{ displayAmount(addAmountCharacter('7')) }
        viewBinding.keypad.numkey7.setOnTouchListener(this)
        viewBinding.keypad.numkey8.setOnClickListener{ displayAmount(addAmountCharacter('8')) }
        viewBinding.keypad.numkey8.setOnTouchListener(this)
        viewBinding.keypad.numkey9.setOnClickListener{ displayAmount(addAmountCharacter('9')) }
        viewBinding.keypad.numkey9.setOnTouchListener(this)
        viewBinding.keypad.numkeyClear.setOnClickListener{ clearAmount() }
        viewBinding.keypad.numkeyClear.setOnTouchListener(this)
        viewBinding.keypad.numkeyBackspace.setOnClickListener{ displayAmount(deleteAmountCharacter()) }
        viewBinding.keypad.numkeyBackspace.setOnTouchListener(this)
        tvAmount = viewBinding.tvAmount
        amt = ""
        displayAmount(amt)
    }

    private fun showFuncKey() {
        viewBinding.keypad.numkeyClear.visibility = View.VISIBLE
        viewBinding.keypad.numkeyBackspace.visibility = View.VISIBLE
        viewBinding.tvSubmit.isEnabled = true
    }

    private fun hideFuncKey() {
        viewBinding.keypad.numkeyClear.visibility = View.INVISIBLE
        viewBinding.keypad.numkeyBackspace.visibility = View.INVISIBLE
        viewBinding.tvSubmit.isEnabled = false
    }

    private fun addAmountCharacter(w: Char): String {
        if (amt.length >= 8) {
            return amt
        }
        if (w == '0' && pattern.matcher(amt).matches()) {
            return amt
        }
        amt += w
        return amt
    }

    private fun deleteAmountCharacter(): String {
        if (amt.isEmpty()) {
            amt = ""
            return amt
        }
        amt = amt.substring(0, amt.length - 1)
        return amt
    }

    private fun clearAmount() {
        amt = ""
        displayAmount(amt)
        return
    }

    private val amount: String
        get() = tvAmount!!.text.toString()

    private fun displayAmount(amt: String?) {
        val value = buildString {
            var remainLength = amt!!.length
            while (remainLength < 3) {
                append('0')
                ++remainLength
            }
            append(amt)
            insert(remainLength - 2, '.')
        }
        
        if("0.00" == value) {
                viewBinding.keypad.numkeyClear.visibility = View.INVISIBLE
                viewBinding.keypad.numkeyBackspace.visibility = View.INVISIBLE
            } else {
                viewBinding.keypad.numkeyClear.visibility = View.VISIBLE
                viewBinding.keypad.numkeyBackspace.visibility = View.VISIBLE
            }
        tvAmount!!.text = "$"+formatAmount(value)

        if (amt!!.isEmpty()) {
            hideFuncKey()
        } else {
            showFuncKey()
        }
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val id = view.id
        var scaleView: View? = null
        if (id == R.id.numkey_0) {
            scaleView = viewBinding.keypad.digit0
        } else if (id == R.id.numkey_1) {
            scaleView = viewBinding.keypad.digit1
        } else if (id == R.id.numkey_2) {
            scaleView = viewBinding.keypad.digit2
        } else if (id == R.id.numkey_3) {
            scaleView = viewBinding.keypad.digit3
        } else if (id == R.id.numkey_4) {
            scaleView = viewBinding.keypad.digit4
        } else if (id == R.id.numkey_5) {
            scaleView = viewBinding.keypad.digit5
        } else if (id == R.id.numkey_6) {
            scaleView = viewBinding.keypad.digit6
        } else if (id == R.id.numkey_7) {
            scaleView = viewBinding.keypad.digit7
        } else if (id == R.id.numkey_8) {
            scaleView = viewBinding.keypad.digit8
        } else if (id == R.id.numkey_9) {
            scaleView = viewBinding.keypad.digit9
        } else if (id == R.id.numkey_clear) {
            scaleView = viewBinding.keypad.tvClear
        } else if (id == R.id.numkey_backspace) {
            scaleView = viewBinding.keypad.tvBackspace
        }
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                scaleView!!.scaleX = 1.5.toFloat()
                scaleView.scaleY = 1.5.toFloat()
            }
            MotionEvent.ACTION_UP -> {
                scaleView!!.scaleX = 1f
                scaleView.scaleY = 1f
            }
            else -> {}
        }
        return false
    }

    private fun createNewPayment(amt: String?) {
            amt?.let {
                activity?.navigateToTarget(
                    CheckoutFragment.TAG,
                    CheckoutFragment.requestPayment(
                    viewBinding.tvAmount.text.toString(),
                    it.toLong(),
                    "usd",
                        skipTipping = false,
                        extendedAuth = false,
                        incrementalAuth = false
                    ), replace = true, addToBackStack = true
                )
        }
    }
}