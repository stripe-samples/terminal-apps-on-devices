package com.stripe.aod.sampleapp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.stripe.aod.sampleapp.R
import com.stripe.aod.sampleapp.databinding.FragmentReceiptBinding
import com.stripe.aod.sampleapp.utils.backToPrevious
import com.stripe.aod.sampleapp.utils.clearBackStack
import com.stripe.aod.sampleapp.utils.navigateToTarget
import com.stripe.aod.sampleapp.utils.replaceFragmentInActivity

class ReceiptFragment : Fragment(R.layout.fragment_receipt) {
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

    private var _viewBinding : FragmentReceiptBinding? = null
    private val viewBinding get() = _viewBinding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewBinding = FragmentReceiptBinding.bind(view)

        viewBinding.rlBack.setOnClickListener {
            activity?.backToPrevious()
        }
        viewBinding.receiptEmail.setOnClickListener {
            activity?.navigateToTarget(EmailFragment.TAG, EmailFragment(),
                replace = true,
                addToBackStack = true
            )
        }
        viewBinding.receiptSms.setOnClickListener {

        }
        viewBinding.receiptPrint.setOnClickListener {

        }
        viewBinding.receiptSkip.setOnClickListener {
            activity?.clearBackStack()
            activity?.replaceFragmentInActivity(HomeFragment(),R.id.container)
        }

        arguments?.let {
            val amountValue = it.getString(AMOUNT)
            viewBinding.totalAmount.text = amountValue
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}