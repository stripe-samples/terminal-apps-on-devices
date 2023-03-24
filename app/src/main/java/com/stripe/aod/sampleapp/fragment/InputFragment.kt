package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.activity.MainActivity
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.navigateToTarget
import kotlinx.android.synthetic.main.fragment_input.*
import kotlinx.android.synthetic.main.widget_digit_keyboard.*
import java.text.DecimalFormat
import java.util.regex.Pattern

class InputFragment : Fragment(R.layout.fragment_input), View.OnClickListener, OnTouchListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.InputFragment"
    }

    private var amt: String? = null
    private var tvAmount: TextView? = null
    private val pattern = Pattern.compile("[0]*")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        //hand back press action
        requireActivity().onBackPressedDispatcher.addCallback(activity as MainActivity,object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.backToPrevious()
            }
        })
    }
    private fun initView() {
        tv_submit.setOnClickListener(this)
        rl_back.setOnClickListener(this)
        numkey_0.setOnClickListener(this)
        numkey_0.setOnTouchListener(this)
        numkey_1.setOnClickListener(this)
        numkey_1.setOnTouchListener(this)
        numkey_2.setOnClickListener(this)
        numkey_2.setOnTouchListener(this)
        numkey_3.setOnClickListener(this)
        numkey_3.setOnTouchListener(this)
        numkey_4.setOnClickListener(this)
        numkey_4.setOnTouchListener(this)
        numkey_5.setOnClickListener(this)
        numkey_5.setOnTouchListener(this)
        numkey_6.setOnClickListener(this)
        numkey_6.setOnTouchListener(this)
        numkey_7.setOnClickListener(this)
        numkey_7.setOnTouchListener(this)
        numkey_8.setOnClickListener(this)
        numkey_8.setOnTouchListener(this)
        numkey_9.setOnClickListener(this)
        numkey_9.setOnTouchListener(this)
        numkey_clear.setOnClickListener(this)
        numkey_clear.setOnTouchListener(this)
        numkey_backspace.setOnClickListener(this)
        numkey_backspace.setOnTouchListener(this)
        tv_submit.setOnClickListener(this);
        tvAmount = tv_amount as TextView
        amt = ""
        displayAmount(amt)
    }

    private fun showFuncKey() {
        numkey_clear.visibility = View.VISIBLE
        numkey_backspace.visibility = View.VISIBLE
        tv_submit.isEnabled = true
    }

    private fun hideFuncKey() {
        numkey_clear.visibility = View.INVISIBLE
        numkey_backspace.visibility = View.INVISIBLE
        tv_submit.isEnabled = false
    }

    private fun addAmt(w: Char): String? {
        if (amt!!.length >= 8) {
            return amt
        }
        if (w == '0' && pattern.matcher(amt).matches()) {
            return amt
        }
        amt += w
        return amt
    }

    private fun subAmt(): String {
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
        val sb = StringBuilder()
        var remainLength = amt!!.length
        while (remainLength < 3) {
            sb.append('0')
            ++remainLength
        }
        sb.append(amt)
        sb.insert(remainLength - 2, '.')
        if("0.00" == sb.toString()) {
            numkey_clear.visibility = View.INVISIBLE
            numkey_backspace.visibility = View.INVISIBLE
        } else {
            numkey_clear.visibility = View.VISIBLE
            numkey_backspace.visibility = View.VISIBLE
        }
        tvAmount!!.text = "$"+formatAmount(sb.toString())

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
            scaleView = digit_0
        } else if (id == R.id.numkey_1) {
            scaleView = digit_1
        } else if (id == R.id.numkey_2) {
            scaleView = digit_2
        } else if (id == R.id.numkey_3) {
            scaleView = digit_3
        } else if (id == R.id.numkey_4) {
            scaleView = digit_4
        } else if (id == R.id.numkey_5) {
            scaleView = digit_5
        } else if (id == R.id.numkey_6) {
            scaleView = digit_6
        } else if (id == R.id.numkey_7) {
            scaleView = digit_7
        } else if (id == R.id.numkey_8) {
            scaleView = digit_8
        } else if (id == R.id.numkey_9) {
            scaleView = digit_9
        } else if (id == R.id.numkey_clear) {
            scaleView = tv_clear
        } else if (id == R.id.numkey_backspace) {
            scaleView = tv_backspace
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
            displayAmount(addAmt('0'))
        } else if (id == R.id.numkey_1) {
            displayAmount(addAmt('1'))
        } else if (id == R.id.numkey_2) {
            displayAmount(addAmt('2'))
        } else if (id == R.id.numkey_3) {
            displayAmount(addAmt('3'))
        } else if (id == R.id.numkey_4) {
            displayAmount(addAmt('4'))
        } else if (id == R.id.numkey_5) {
            displayAmount(addAmt('5'))
        } else if (id == R.id.numkey_6) {
            displayAmount(addAmt('6'))
        } else if (id == R.id.numkey_7) {
            displayAmount(addAmt('7'))
        } else if (id == R.id.numkey_8) {
            displayAmount(addAmt('8'))
        } else if (id == R.id.numkey_9) {
            displayAmount(addAmt('9'))
        } else if (id == R.id.numkey_backspace) {
            displayAmount(subAmt())
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