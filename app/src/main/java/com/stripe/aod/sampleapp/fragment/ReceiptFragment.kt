package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.clearBackStack
import com.stripe.aod.sampleapp.utils.navigateToTarget
import com.stripe.aod.sampleapp.utils.replaceFragmentInActivity
import kotlinx.android.synthetic.main.fragment_receipt.*

class ReceiptFragment: Fragment(R.layout.fragment_receipt),View.OnClickListener {

    companion object {
        const val TAG = "com.stripe.aod.sampleapp.fragment.ReceiptFragment"
        private const val AMOUNT = "com.stripe.aod.sampleapp.fragment.CheckoutFragment.amount"

        fun requestPayment(amount: String): ReceiptFragment {
            val fragment = ReceiptFragment()
            val bundle = Bundle()
            bundle.putString(AMOUNT, amount)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rl_back.setOnClickListener(this)
        receipt_email.setOnClickListener(this)
        receipt_sms.setOnClickListener(this)
        receipt_print.setOnClickListener(this)
        receipt_no_tip.setOnClickListener(this)

        arguments?.let {
            val amountValue = it.getString(AMOUNT)
            total.text = amountValue
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.rl_back -> {
                activity?.backToPrevious()
            }

            R.id.receipt_email -> {
                activity?.navigateToTarget(EmailFragment.TAG, EmailFragment(),true,true)
            }

            R.id.receipt_sms -> {

            }
            R.id.receipt_print -> {

            }
            R.id.receipt_no_tip -> {
                activity?.clearBackStack()
                activity?.replaceFragmentInActivity(HomeFragment(),R.id.container)
            }
        }
    }
}