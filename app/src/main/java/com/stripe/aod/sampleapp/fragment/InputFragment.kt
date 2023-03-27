package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.databinding.FragmentInputBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.navigateToTarget
import java.text.DecimalFormat
import java.util.regex.Pattern

class InputFragment : Fragment(R.layout.fragment_input), View.OnClickListener, OnTouchListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.InputFragment"
    }

    private var amt: String = ""
    private var tvAmount: TextView? = null
    private val pattern = Pattern.compile("[0]*")
    private var _viewBinding : FragmentInputBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentInputBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
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

    private fun initView() {
        viewBinding.tvSubmit.setOnClickListener(this)
        viewBinding.rlBack.setOnClickListener(this)
        viewBinding.keypad.numkey0.setOnClickListener(this)
        viewBinding.keypad.numkey0.setOnTouchListener(this)
        viewBinding.keypad.numkey1.setOnClickListener(this)
        viewBinding.keypad.numkey1.setOnTouchListener(this)
        viewBinding.keypad.numkey2.setOnClickListener(this)
        viewBinding.keypad.numkey2.setOnTouchListener(this)
        viewBinding.keypad.numkey3.setOnClickListener(this)
        viewBinding.keypad.numkey3.setOnTouchListener(this)
        viewBinding.keypad.numkey4.setOnClickListener(this)
        viewBinding.keypad.numkey4.setOnTouchListener(this)
        viewBinding.keypad.numkey5.setOnClickListener(this)
        viewBinding.keypad.numkey5.setOnTouchListener(this)
        viewBinding.keypad.numkey6.setOnClickListener(this)
        viewBinding.keypad.numkey6.setOnTouchListener(this)
        viewBinding.keypad.numkey7.setOnClickListener(this)
        viewBinding.keypad.numkey7.setOnTouchListener(this)
        viewBinding.keypad.numkey8.setOnClickListener(this)
        viewBinding.keypad.numkey8.setOnTouchListener(this)
        viewBinding.keypad.numkey9.setOnClickListener(this)
        viewBinding.keypad.numkey9.setOnTouchListener(this)
        viewBinding.keypad.numkeyClear.setOnClickListener(this)
        viewBinding.keypad.numkeyClear.setOnTouchListener(this)
        viewBinding.keypad.numkeyBackspace.setOnClickListener(this)
        viewBinding.keypad.numkeyBackspace.setOnTouchListener(this)
        viewBinding.tvSubmit.setOnClickListener(this);
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

    private fun addAmountCharacter(w: Char): String? {
        if (amt!!.length >= 8) {
            return amt
        }
        if (w == '0' && pattern.matcher(amt).matches()) {
            return amt
        }
        amt += w
        return amt
    }

    private fun deleteAmountCharacter(): String {
        if (amt == null || amt!!.isEmpty()) {
            amt = ""
            return amt!!
        }
        amt = amt!!.substring(0, amt!!.length - 1)
        return amt as String
    }

    fun clearAmount() {
        amt = ""
        displayAmount(amt)
        return
    }

    val amount: String
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

        if (TextUtils.isEmpty(amt)) {
            hideFuncKey()
        } else {
            showFuncKey()
        }
    }

    private fun formatAmount(amt: String?): String? {
        val decimalFormat = DecimalFormat("#,##0.00")
        return decimalFormat.format(amt!!.toBigDecimal())
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

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.numkey_0) {
            displayAmount(addAmountCharacter('0'))
        } else if (id == R.id.numkey_1) {
            displayAmount(addAmountCharacter('1'))
        } else if (id == R.id.numkey_2) {
            displayAmount(addAmountCharacter('2'))
        } else if (id == R.id.numkey_3) {
            displayAmount(addAmountCharacter('3'))
        } else if (id == R.id.numkey_4) {
            displayAmount(addAmountCharacter('4'))
        } else if (id == R.id.numkey_5) {
            displayAmount(addAmountCharacter('5'))
        } else if (id == R.id.numkey_6) {
            displayAmount(addAmountCharacter('6'))
        } else if (id == R.id.numkey_7) {
            displayAmount(addAmountCharacter('7'))
        } else if (id == R.id.numkey_8) {
            displayAmount(addAmountCharacter('8'))
        } else if (id == R.id.numkey_9) {
            displayAmount(addAmountCharacter('9'))
        } else if (id == R.id.numkey_backspace) {
            displayAmount(deleteAmountCharacter())
        } else if (id == R.id.numkey_clear) {
            clearAmount()
		} else if (id == R.id.tv_submit) {
            amt?.let {
                activity?.navigateToTarget(
                    CheckoutFragment.TAG,
                    CheckoutFragment.requestPayment(
                    it.toLong(),
                    "usd",
                    false,
                    false,
                    false
                    ), true, true)
            }
        } else if (id == R.id.rl_back) {
            activity?.backToPrevious()
        }
    }
}